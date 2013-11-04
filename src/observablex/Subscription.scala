package observablex

import rx.lang.scala.Subscription
import rx.subscriptions.Subscriptions
import rx.util.functions.Action0


object Subscription {

  def apply(subscription: rx.Subscription) : Subscription = subscription

  def apply(unsubscribe: => Unit) : Subscription = {
    Subscriptions.create(new Action0(){
     def call() { unsubscribe }
    })
  }
}
