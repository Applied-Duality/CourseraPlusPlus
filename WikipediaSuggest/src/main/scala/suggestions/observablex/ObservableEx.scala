package suggestions
package observablex

import scala.concurrent.{Future, ExecutionContext}
import scala.util._
import scala.util.Success
import scala.util.Failure
import java.lang.Throwable
import rx.lang.scala.Observable
import rx.lang.scala.Scheduler

object ObservableEx {

  def apply[T](f: Future[T])(implicit execContext: ExecutionContext): Observable[T] = {
    val s = rx.subjects.AsyncSubject.create[T]()
    f.onComplete {
      case Failure(e) => { s.onError(e) }
      case Success(c) => { s.onNext(c); s.onCompleted() }
    }
    Observable(s)
  }

  def apply()(implicit s: Scheduler): Observable[Unit]  = {
    Observable(observer => {
      s.scheduleRec(self => {
        observer.onNext(())
        self
      })
    })
  }

  def apply[T](ss: Iterable[T])(implicit s: Scheduler): Observable[T] = {
    Observable(observer => {
      s.schedule({
        () => Try(ss.foreach(observer.onNext(_))) match {
          case Success(_) => { observer.onCompleted() }
          case Failure(error) => { observer.onError(error) }
        }
      })
    })
  }

}





