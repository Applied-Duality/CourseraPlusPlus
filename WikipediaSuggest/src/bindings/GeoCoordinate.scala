package bindings

import org.json4s.JValue


object GeoCoordinate {
  def apply(raw: JValue): GeoCoordinate = {
    new GeoCoordinate(raw)
  }
}

class GeoCoordinate(raw: JValue) {
  implicit val formats = org.json4s.DefaultFormats

  private def json: JValue  = raw

  lazy val Latitude: Double = { (json\"geometry"\"coordinates")(1).extract[Double] }
  lazy val Longitude  : Double = { (json\"geometry"\"coordinates")(0).extract[Double] }
  lazy val Altitude  : Double = { (json\"geometry"\"coordinates")(2).extract[Double] }

  override def toString(): String = s"{ 'longitude':'${Longitude}', 'latitude':'${Latitude}', 'altitude':'${Altitude}' }";
}
