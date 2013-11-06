package suggestions
package gui

import scala.collection.mutable.ListBuffer
import scala.swing._
import swing.Swing._
import scala.collection.JavaConverters._
import Orientation._
import java.lang.String
import scala.Predef.String
import observablex.SubscriptionEx
import rx.subscriptions.CompositeSubscription
import rx.lang.scala.Observable
import scala.concurrent.ExecutionContext.Implicits.global
import observablex._
import search._
import scala.util.{ Try, Success, Failure }
import scala.swing.event._
import javax.swing.UIManager

class WikipediaSuggestUtils {

  // TO IMPLEMENT Students implement this
  /** Returns a stream of text field values entered in the given text field.
   *
   *  @param field       the text field
   *  @return            an observable with a stream of text field updates
   */
  def textFieldValues(field: TextField): Observable[String] = {
    Observable(observer => {
      val onChanged = Reaction {
        case value: ValueChanged =>
          val s = value.source.asInstanceOf[TextField].text
          observer.onNext(s)
        case _ =>
      }
      field.subscribe(onChanged)
      SubscriptionEx {
        field.unsubscribe(onChanged)
      }
    })
  }

  // TO IMPLEMENT Students implement this
  /** Returns a stream of button clicks.
   *
   *  @param field       the button
   *  @return            an observable with a stream of buttons that have been clicked
   */
  def buttonClicks(button: Button): Observable[AbstractButton] = {
    Observable(observer => {
      val onClicked = Reaction {
        case clicked: ButtonClicked => observer.onNext(clicked.source)
        case _                      => 
      }
      button.subscribe(onClicked)
      SubscriptionEx {
        button.unsubscribe(onClicked)
      }
    })
  }

  def wikiSuggestResponseStream(term: String) = ObservableEx(Search.wikipediaSuggestion(term))

  def wikiPageResponseStream(term: String) = ObservableEx(Search.wikipediaPage(term))

  def tryStream[T](s: Observable[T]) = s map { Success(_) } onErrorReturn {
    t => Failure(t)
  }

  def responseStream[A, B](requestStream: Observable[A], requestMethod: A => Observable[B]): Observable[Try[B]] =
    requestStream map { term => 
      tryStream(requestMethod(term))
    } flatten

  def validStream(s: Observable[String]) = s.map(_.replace(" ", "_"))

}

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
    minimumSize = new Dimension(800, 600)

    val wikiUtils = new WikipediaSuggestUtils
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
          maximumSize = new Dimension(240, 800)
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

    val clickStream = wikiUtils.buttonClicks(button).map { _ =>
      if (list.selection.items.nonEmpty) list.selection.items.head else ""
    }

    val textStream = wikiUtils.textFieldValues(text)

    val suggestionStream = wikiUtils.responseStream(wikiUtils.validStream(textStream), wikiUtils.wikiSuggestResponseStream)

    val pageStream = wikiUtils.responseStream(wikiUtils.validStream(clickStream), wikiUtils.wikiPageResponseStream)

    val pageSubscription = pageStream.observeOn(eventScheduler) subscribe {
      _ match {
        case Success(response) =>
          status.text = " "
          editorpane.text = response
        case Failure(t) =>
          status.text = "Error occurred: " + t.getMessage
      }
    }

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
