package search

import bindings.{Country, GeoCoordinate, EarthQuake}
import org.json4s._
import scala.concurrent.{ ExecutionContext, Future }
import scala.language.postfixOps
import dispatch._
import org.json4s.native._
import scala.util.Try

import scala.async.Async._
import ExecutionContext.Implicits.global
import rx.lang.scala.Observable
import observablex.{Scheduler, ObservableEx}
import ObservableEx._
import scala.collection.JavaConversions

object Search {

  implicit val formats = org.json4s.DefaultFormats
  implicit val s: Scheduler = Scheduler.NewThreadScheduler

  def wikipedia(term: String): Future[List[String]] = {
    async {
      val search = "http://en.wikipedia.org/w/api.php?action=opensearch&format=json&search="
      val response = await { Http(url(search+term).OK(as.String)) }
      val json = JsonParser.parse(response)
      val words = json(1)
      words.extract[List[String]]
    }
  }

  def google(term: String): Future[List[String]] = {

    async {
      val search = "http://suggestqueries.google.com/complete/search?client=firefox&q="
      val response = await { Http(url(search+term).OK(as.String)) }
      val json = JsonParser.parse(response)
      val words = json(1)
      words.extract[List[String]]
    }
  }

  def reverseGeoCode(location: GeoCoordinate): Future[Country] = {

    async {
      val service = "http://ws.geonames.org/countrySubdivisionJSON?"
      val response = await { Http(url(service+s"lat=${location.Latitude}&lng=${location.Longitude}").OK(as.String)) }
      val json = JsonParser.parse(response)
      Country(json)
    }

  }

  def usgs() : Future[List[EarthQuake]] = {

    async {
      val quakes = "http://earthquake.usgs.gov/earthquakes/feed/geojson/all/day"
      val response = await {  Http(url(quakes).OK(as.String)) }
      val json = (JsonParser.parse(response)\"features").children
      Try(json.map(EarthQuake(_))).getOrElse(List())
    }

  }

  def quakesWithCountry() : Observable[(EarthQuake, Country)] = {
    for {
      qs <- ObservableEx(Search.usgs())
      q <-  ObservableEx(qs) (s)
      l <-  ObservableEx(Search.reverseGeoCode(q.Location))
    } yield {
      (q,l)
    }
  }
}
