package suggestions
package gui

import rx.lang.scala.Observable
import observablex.SubscriptionEx
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Try, Success, Failure }
import scala.swing.Reactions.Reaction
import scala.swing.event.Event

/** Basic facilities for dealing with Swing-like components.
 *
 *  Instead of committing to a particular widget implementation
 *  functionality has been factored out here to deal only with
 *  abstract types like `ValueChanged` or `TextField`.
 *  Extractors for abstract events like `ValueChanged` have also
 *  been factored out into corresponding abstract `val`s.
 */
trait SwingApi {

  type ValueChanged <: Event

  val ValueChanged: {
    def unapply(x: Event): Option[TextField]
  }

  type ButtonClicked <: Event

  val ButtonClicked: {
    def unapply(x: Event): Option[Button]
  }

  type TextField <: {
    def text: String
    def subscribe(r: Reaction): Unit
    def unsubscribe(r: Reaction): Unit
  }

  type Button <: {
    def subscribe(r: Reaction): Unit
    def unsubscribe(r: Reaction): Unit
  }

  // TO IMPLEMENT Students implement this
  /** Returns a stream of text field values entered in the given text field.
   *
   *  @param field       the text field
   *  @return            an observable with a stream of text field updates
   */
  def textFieldValues(field: TextField): Observable[String] = {
    Observable(observer => {
      val onChanged = Reaction {
        case ValueChanged(textField) =>
          val s = textField.text
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
  def buttonClicks(button: Button): Observable[Button] = {
    Observable(observer => {
      val onClicked = Reaction {
        case ButtonClicked(source) =>
          observer.onNext(source)
        case _                     => 
      }
      button.subscribe(onClicked)
      SubscriptionEx {
        button.unsubscribe(onClicked)
      }
    })
  }

}