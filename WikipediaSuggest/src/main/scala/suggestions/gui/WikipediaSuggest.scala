package suggestions
package gui

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.swing._
import scala.util.{ Try, Success, Failure }
import scala.swing.event._
import swing.Swing._
import javax.swing.UIManager
import Orientation._
import observablex.SubscriptionEx
import rx.subscriptions.CompositeSubscription
import rx.lang.scala.Observable
import rx.lang.scala.Subscription
import observablex._
import search._

object WikipediaSuggest extends SimpleSwingApplication {

  {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch {
      case t: Throwable =>
    }
  }

  def top = new MainFrame {

    /* gui setup */

    title = "Query Wikipedia"
    minimumSize = new Dimension(900, 600)

    val button = new Button("Get") {
      icon = new javax.swing.ImageIcon(javax.imageio.ImageIO.read(this.getClass.getResourceAsStream("/wiki-icon.png")))
    }
    val searchTermField = new TextField
    val suggestionList = new ListView(ListBuffer[String]())
    val status = new Label(" ")
    val editorpane = new EditorPane {
      import javax.swing.border._
      border = new EtchedBorder(EtchedBorder.LOWERED)
      editable = false
      peer.setContentType("text/html")
    }

    contents = new BoxPanel(orientation = Vertical) {
      border = EmptyBorder(top = 5, left = 5, bottom = 5, right = 5)
      contents += new BoxPanel(orientation = Horizontal) {
        contents += new BoxPanel(orientation = Vertical) {
          maximumSize = new Dimension(240, 900)
          border = EmptyBorder(top = 10, left = 10, bottom = 10, right = 10)
          contents += new BoxPanel(orientation = Horizontal) {
            maximumSize = new Dimension(640, 30)
            border = EmptyBorder(top = 5, left = 0, bottom = 5, right = 0)
            contents += searchTermField
          }
          contents += new ScrollPane(suggestionList)
          contents += new BorderPanel {
            maximumSize = new Dimension(640, 30)
            add(button, BorderPanel.Position.Center)
          }
        }
        contents += new ScrollPane(editorpane)
      }
      contents += status
    }

    val eventScheduler = SchedulerEx.SwingEventThreadScheduler

    /* observables */

    // TO IMPLEMENT
    val searchTerms: Observable[String] = swingApi.textFieldValues(searchTermField)

    // TO IMPLEMENT
    val suggestions: Observable[Try[List[String]]] = wikiApi.responseStream(wikiApi.validStream(searchTerms), wikiApi.wikiSuggestResponseStream)

    // TO IMPLEMENT
    val suggestionSubscription: Subscription = suggestions.observeOn(eventScheduler) subscribe {
      _ match {
        case Success(responses) =>
          status.text = " "
          suggestionList.listData = responses
        case Failure(t) =>
          status.text = "Error occurred: " + t.getMessage
          suggestionList.listData = Nil
      }
    }

    // TO IMPLEMENT
    val clicks: Observable[String] = swingApi.buttonClicks(button) map { _ =>
      if (suggestionList.selection.items.nonEmpty) suggestionList.selection.items.head else ""
    } filter (_ != "")

    // TO IMPLEMENT
    val pages: Observable[Try[String]] = wikiApi.responseStream(wikiApi.validStream(clicks), wikiApi.wikiPageResponseStream)

    // TO IMPLEMENT
    val pageSubscription: Subscription = pages.observeOn(eventScheduler) subscribe {
      _ match {
        case Success(response) =>
          status.text = " "
          editorpane.text = response
        case Failure(t) =>
          status.text = "Error occurred: " + t.getMessage
      }
    }

  }

  object wikiApi extends WikipediaApi {
    def wikipediaSuggestion(term: String) = Search.wikipediaSuggestion(term)
    def wikipediaPage(term: String) = Search.wikipediaPage(term)
  }

  object swingApi extends SwingApi {
    type ValueChanged = scala.swing.event.ValueChanged
    object ValueChanged {
      def unapply(x: Event) = x match {
        case vc: ValueChanged => Some(vc.source.asInstanceOf[TextField])
        case _ => None
      }
    }
    type ButtonClicked = scala.swing.event.ButtonClicked
    object ButtonClicked {
      def unapply(x: Event) = x match {
        case bc: ButtonClicked => Some(bc.source.asInstanceOf[Button])
        case _ => None
      }
    }
    type TextField = scala.swing.TextField
    type Button = scala.swing.Button
  }


}
