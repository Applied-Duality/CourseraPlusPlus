package suggestions

import scala.swing._
import scala.swing.Reactions.Reaction
import scala.swing.event.{ValueChanged, ButtonClicked}
import rx.lang.scala.Observable
import observablex.SubscriptionEx

package object gui {

  object Reaction {
    def apply(r: Reaction) = r
  }

  def textBoxValues(textField: TextField): Observable[String] = {
    Observable(observer => {
      val onChanged = Reaction {
        case value: ValueChanged =>
          val s = value.source.asInstanceOf[TextField].text
          observer.onNext(s)
        case _ =>
      }
      textField.subscribe(onChanged)
      SubscriptionEx {
        textField.unsubscribe(onChanged)
      }
    })
  }

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