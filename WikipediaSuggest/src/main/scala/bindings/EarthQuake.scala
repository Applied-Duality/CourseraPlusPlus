package bindings

import java.util.Date
import scala.util.Try
import org.json4s.JValue
import observablex.Scheduler

object EarthQuake {
  def apply(raw: JValue): EarthQuake = new EarthQuake(raw)
}
class EarthQuake(raw: JValue) {

  implicit val formats = org.json4s.DefaultFormats
  implicit val s = Scheduler.NewThreadScheduler

  private def json: JValue  = raw

  lazy val Place: String = (json\"properties"\"place").extract[String]
  lazy val Magnitude: Double =  (json\"properties"\"mag").extract[Double]
  lazy val Location: GeoCoordinate = GeoCoordinate(raw)
  lazy val Time: Date = new Date(java.lang.Long.parseLong((json\"properties"\"time").extract[String]))

  override def toString(): String =
    s"{ 'time':'${Time}', 'place':'${Place}', 'magnitude':'${Magnitude}', 'coordinates':'${Location}' }";
}

