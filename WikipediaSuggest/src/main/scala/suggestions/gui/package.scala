package suggestions

import rx.lang.scala.Observable
import observablex.SubscriptionEx
import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.swing.Reactions.Reaction
import scala.swing._
import scala.util.{ Try, Success, Failure }
import scala.swing.event._
import swing.Swing._
import javax.swing.UIManager

package object gui {

  object Reaction {
    def apply(r: Reaction) = r
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

}