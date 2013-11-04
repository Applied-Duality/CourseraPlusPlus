package observablex

import java.util.concurrent.Executor
import rx.lang.scala.{ImplicitFunctionConversions, Subscription}
import rx.util.functions.Action0

object Scheduler {
  lazy val NewThreadScheduler = Scheduler(rx.concurrency.NewThreadScheduler.getInstance())

  def apply(exec: Executor): Scheduler = {
    Scheduler(new rx.concurrency.ExecutorScheduler(exec))
  }

  def apply(s: rx.Scheduler): Scheduler = {
    new Scheduler{ def inner = s}
  }
}

trait Scheduler {

  def inner: rx.Scheduler

  def schedule(work: => Unit): Subscription = {
   inner.schedule(new Action0(){
      def call(){ work }
    })
  }

  def schedule(work: Scheduler=>Subscription): Subscription = {
    val f = ImplicitFunctionConversions.scalaFunction2ToRxFunc2((x: rx.Scheduler,e: Int) => {
      work(Scheduler(x))
    })
    Subscription(inner.schedule(0,f))
  }

  def scheduleRec(work: (=>Unit)=>Unit): Subscription = {

    val subscription = new rx.subscriptions.MultipleAssignmentSubscription()

    subscription.setSubscription(
      schedule(scheduler => {
        def loop(): Unit =  subscription.setSubscription(scheduler.schedule{ work{ loop() }})
        loop()
        subscription
      }))
    subscription
  }

}
