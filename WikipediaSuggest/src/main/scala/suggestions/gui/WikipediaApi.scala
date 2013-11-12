package suggestions
package gui

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConverters._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Try, Success, Failure }
import observablex.SubscriptionEx
import rx.subscriptions.CompositeSubscription
import rx.lang.scala.Observable
import observablex._
import search._

trait WikipediaApi {

  /** Returns a `Future` with a list of possible completions for a search `term`.
   */
  def wikipediaSuggestion(term: String): Future[List[String]]

  /** Returns a `Future` with the contents of the Wikipedia page for the given search `term`.
   */
  def wikipediaPage(term: String): Future[String]

  /** Returns an `Observable` with a list of possible completions for a search `term`.
   */
  def wikiSuggestResponseStream(term: String) = ObservableEx(wikipediaSuggestion(term))

  /** Returns an `Observable` with the contents of the Wikipedia page for the given search `term`.
   */
  def wikiPageResponseStream(term: String) = ObservableEx(wikipediaPage(term))

  // TO IMPLEMENT
  /** Given a stream of search terms, returns a stream of search terms with spaces replaced by underscores.
   *
   *  E.g. `"erik", "erik meijer", "martin` should become `"erik", "erik_meijer", "martin"`
   */
  def validStream(s: Observable[String]) = s.map(_.replace(" ", "_"))

  // TO IMPLEMENT
  /** Given an observable that can possibly be completed with an error, returns a new observable
   *  with the same values wrapped into `Success` and the potential error wrapped into `Failure`.
   *  
   *  E.g. `1, 2, 3, !Exception!` should become `Success(1), Success(2), Success(3), Failure(Exception)`
   */
  def tryStream[T](s: Observable[T]) = s map { Success(_) } onErrorReturn {
    t => Failure(t)
  }

  // TO IMPLEMENT
  /** Given a stream of requests `requestStream` and a method `requestMethod` to map a request `T` into 
   *  a stream of responses `S`, returns a stream of all the responses wrapped into a `Try`.
   *
   *  E.g. given a request stream:
   *  
   *      1, 2, 3, 4, 5
   *
   *  And a request method:
   *
   *      num => if (num != 4) Observable.just(num) else Observable.error(new Exception)
   *
   *  We should, for example, get:
   *
   *      Success(1), Success(2), Success(3), Failure(new Exception), Success(5)
   *
   */
  def responseStream[A, B](requestStream: Observable[A], requestMethod: A => Observable[B]): Observable[Try[B]] =
    requestStream map { term => 
      tryStream(requestMethod(term))
    } flatten

}

