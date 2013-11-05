package suggestions
package observablex

import java.util.concurrent.Executor
import rx.lang.scala.{ ImplicitFunctionConversions, Subscription }
import rx.util.functions.Action0
import rx.lang.scala.Scheduler

object SchedulerEx {
  lazy val NewThreadScheduler = Scheduler(rx.concurrency.NewThreadScheduler.getInstance())

  val SwingEventThreadScheduler: Scheduler = new Scheduler {
    import rx.util.functions.Func2
    def timer = new java.util.Timer
    def asJava = new rx.Scheduler {
      def schedule[T](state: T, func: rx.util.functions.Func2[_ >: rx.Scheduler, _ >: T, _ <: rx.Subscription], delay: Long, units: java.util.concurrent.TimeUnit): rx.Subscription = {
        timer.schedule(new java.util.TimerTask{
          def run() = SwingEventThreadScheduler.asJava.schedule[T](state, func)
        }, units.toMillis(delay))

        new rx.Subscription {
          def unsubscribe() {}
        }
      }
      def schedule[T](state: T, func: rx.util.functions.Func2[_ >: rx.Scheduler, _ >: T, _ <: rx.Subscription]): rx.Subscription = {
        javax.swing.SwingUtilities.invokeLater(new Runnable {
          def run() {
            func.call(SwingEventThreadScheduler.asJava, state)
          }
        })

        new rx.Subscription {
          def unsubscribe() {}
        }
      }
    }
  }

  def apply(exec: Executor): Scheduler = {
    Scheduler(new rx.concurrency.ExecutorScheduler(exec))
  }

  def apply(s: rx.Scheduler): Scheduler = {
    new Scheduler{ def asJava = s}
  }

}