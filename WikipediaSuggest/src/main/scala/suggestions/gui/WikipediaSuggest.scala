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

    object wikiApi extends WikipediaApi {
      def wikipediaSuggestion(term: String) = Search.wikipediaSuggestion(term)
      def wikipediaPage(term: String) = Search.wikipediaPage(term)
    }

    val button = new Button("Get") {
      icon = new javax.swing.ImageIcon(javax.imageio.ImageIO.read(this.getClass.getResourceAsStream("/wiki-icon.png")))
    }
    val text = new TextField
    val list = new ListView(ListBuffer[String]())
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
            contents += text
          }
          contents += new ScrollPane(list)
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
    val clickStream = buttonClicks(button).map { _ =>
      if (list.selection.items.nonEmpty) list.selection.items.head else ""
    }

    // TO IMPLEMENT
    val textStream = textFieldValues(text)

    // TO IMPLEMENT
    val suggestionStream = wikiApi.responseStream(wikiApi.validStream(textStream), wikiApi.wikiSuggestResponseStream)

    // TO IMPLEMENT
    val pageStream = wikiApi.responseStream(wikiApi.validStream(clickStream), wikiApi.wikiPageResponseStream)

    // TO IMPLEMENT
    val pageSubscription = pageStream.observeOn(eventScheduler) subscribe {
      _ match {
        case Success(response) =>
          status.text = " "
          editorpane.text = response
        case Failure(t) =>
          status.text = "Error occurred: " + t.getMessage
      }
    }

    // TO IMPLEMENT
    val suggestionSubscription = suggestionStream.observeOn(eventScheduler) subscribe {
      _ match {
        case Success(responses) =>
          status.text = " "
          list.listData = responses
        case Failure(t) =>
          status.text = "Error occurred: " + t.getMessage
          list.listData = Nil
      }
    }

  }

}
