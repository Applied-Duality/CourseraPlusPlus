package suggestions
package observablex

import rx.lang.scala.Subscription
import rx.util.functions.Action0
import rx.subscriptions.Subscriptions

object SubscriptionEx {

  def apply(subscription: rx.Subscription) : Subscription = subscription

  def apply(unsubscribe: => Unit) : Subscription = {
    Subscriptions.create(new Action0(){
      def call() { unsubscribe }
    })
  }
}
