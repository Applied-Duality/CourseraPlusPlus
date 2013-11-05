package suggestions

import scala.swing._
import scala.swing.Reactions.Reaction
import rx.lang.scala.Observable
import observablex.SubscriptionEx

package object gui {

  object Reaction {
    def apply(r: Reaction) = r
  }

}