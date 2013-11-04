package observablex

import rx.lang.scala._
import java.util.concurrent.Executor

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
  import ImplicitFunctionConversions._

  def inner: rx.Scheduler

  def schedule(work: => Unit): Subscription = {
    inner.schedule(() => work)
  }

  def schedule(work: Scheduler=>Subscription): Subscription = {
    val f = scalaFunction2ToRxFunc2((x: rx.Scheduler,e: Int) => {
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
