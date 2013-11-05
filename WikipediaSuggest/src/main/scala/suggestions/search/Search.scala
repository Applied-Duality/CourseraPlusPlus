package suggestions
package search

import org.json4s._
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.postfixOps
import dispatch._
import org.json4s.native._
import scala.util.Try

import scala.async.Async._
import ExecutionContext.Implicits.global
import rx.lang.scala.Observable
import observablex.{SchedulerEx, ObservableEx}
import ObservableEx._
import scala.collection.JavaConversions

object Search {

  implicit val formats = org.json4s.DefaultFormats

  def wikipediaSuggestion(term: String): Future[List[String]] = {
    async {
      log("querying: " + term)
      val search = "http://en.wikipedia.org/w/api.php?action=opensearch&format=json&limit=15&search="
      val response = await { Http(url(search + term).OK(as.String)) }
      val json = JsonParser.parse(response)
      val words = json(1)
      words.extract[List[String]]
    }
  }

  def wikipediaPage(term: String): Future[String] = {
    async {
      val search = "http://en.wikipedia.org/w/api.php?action=parse&format=json&prop=text&section=0&page="
      val response = await { Http(url(search + term).OK(as.String)) }
      val json = JsonParser.parse(response)
      val text = for {
        JObject(child) <- json
        JField("parse", JObject(fields)) <- child
        JField("text", JObject(tfields)) <- fields
        JField("*", JString(text)) <- tfields
      } yield text
      text.head
    }
  }

}
