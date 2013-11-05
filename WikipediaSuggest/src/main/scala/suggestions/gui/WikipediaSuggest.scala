package suggestions
package gui

import scala.collection.mutable.ListBuffer
import scala.swing._
import swing.Swing._
import Orientation._
import java.lang.String
import scala.Predef.String
import observablex.Subscription
import rx.subscriptions.CompositeSubscription
import rx.lang.scala.Observable
import scala.concurrent.ExecutionContext.Implicits.global
import observablex._
import search._
import scala.util.{ Try, Success, Failure }

object WikipediaSuggest extends SimpleSwingApplication {

  def top = new MainFrame {

    title = "Query Wikipedia"

    val button = new Button("Get")
    val text = new TextField(columns = 60)
    val list = new ListView(ListBuffer[String]())
    val status = new Label(" ")

    contents = new BoxPanel(orientation = Vertical) {
      border = EmptyBorder(top = 30, left = 30, bottom = 30, right = 30)
      contents += new BoxPanel(orientation = Horizontal) {
        contents += text
        contents += button
      }
      contents += status
      contents += new ScrollPane(list)
    }

    val eventScheduler = SchedulerEx.SwingEventThreadScheduler

    /* observables */

    val clickStream = buttonClicks(button)

    val textStream = textBoxValues(text)

    def wikiResponseStream(term: String) = ObservableEx(Search.wikipedia(term))

    def tryStream[T](s: Observable[T]) = {
      s map { Success(_) } onErrorReturn {
        t => Failure(t)
      }
    }

    val responseStream: Observable[Try[List[String]]] = textStream map {
      term => tryStream(wikiResponseStream(term)).observeOn(eventScheduler)
    } concat

    val responseSubscription = responseStream subscribe { _ match {
      case Success(responses) =>
        status.text = " "
        list.listData = responses
      case Failure(t) =>
        status.text = "Error occurred: " + t.getMessage
        list.listData = Nil
    }}

  }

}
