package suggestions

import rx.lang.scala.Observable
import rx.lang.scala._
import observablex._

package object observablex {

  implicit class SchedulerOps(val scheduler: Scheduler) extends AnyVal {

    def schedule(work: => Unit): Subscription = {
      scheduler.schedule(() => work)
    }
  
    def schedule(work: Scheduler => Subscription): Subscription = {
      SubscriptionEx(scheduler.schedule(0, (x: Scheduler, e: Int) => {
        work(scheduler)
      }))
    }
  
    def scheduleRec(work: (=>Unit) => Unit): Subscription = {
      val subscription = new rx.subscriptions.MultipleAssignmentSubscription()
  
      subscription.setSubscription(
        schedule(scheduler => {
          def loop(): Unit = subscription.setSubscription(scheduler.schedule {
            work {
              loop()
            }
          })
          loop()
          subscription
        }))
      subscription
    }

  }

}