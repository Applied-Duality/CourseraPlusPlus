package bindings

import com.google.gson.annotations.SerializedName
import java.util.Date

class FeatureCollection {
  var metadata : MetaData = _
  var features : Array[Feature] = _

  override def toString(): String =
    s"{ 'metadata':'${metadata}', 'features':'$features' }";

}

class MetaData {
  var url: String = ""
  var count: Integer = _
  var title: String = ""

  override def toString(): String =
    s"{ 'url':'${url}', 'title':'${title}' }";

}

class Feature {
  var properties : Properties = _

  var geometry: Point = _

  override def toString(): String =
    s"{ 'properties':'${properties}', 'geometry':'${geometry}' }";

}

class Properties {

  var place: String = ""

  @SerializedName("time")
  private var _time: Long = _

  @transient
  lazy val time: Date = new Date(_time)

  var magnitude: Double = _

  override def toString(): String =
    s"{ 'time':'${time}', 'place':'${place}', 'magnitude':'${magnitude}' }";

}

class Point {
  private var coordinates: Array[Double] = _

  @transient
  lazy val Latitude: Double = coordinates(1)
  @transient
  lazy val Longitude  : Double = coordinates(0)
  @transient
  lazy val Altitude  : Double  = coordinates(2)

  override def toString(): String = s"{ 'longitude':'${Longitude}', 'latitude':'${Latitude}', 'altitude':'${Altitude}' }";

}
