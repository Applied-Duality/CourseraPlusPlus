package bindings

import com.google.gson.annotations.SerializedName
import java.util.Date

class FeatureCollection {
  val metadata : MetaData = null
  val features : Array[Feature] = null

  override def toString() = s"{ 'metadata':'${metadata}', 'features':[${features.map(_.toString()).reduceLeft((x,s)=> s"$x,\n $s")}] }";
}

class MetaData {
  val url: String = null
  val title: String = null

  override def toString() = s"{ 'url':'${url}', 'title':'${title}' }";
}

class Feature {
  val properties : Properties = null
  val geometry: Point = null
  override def toString() = s"{ 'properties':'${properties}', 'geometry':'${geometry}' }";
}

class Properties {
  val place: String = null
  @SerializedName("time")
  private val _time: Long = 0L
  @transient
  lazy val time: Date = new Date(_time)
  val magnitude: Double = 0D

  override def toString() = s"{ 'time':'${time}', 'place':'${place}', 'magnitude':'${magnitude}' }";

}

class Point {
  private val coordinates: Array[Double] = null
  lazy val Latitude: Double = coordinates(1)
  lazy val Longitude  : Double = coordinates(0)
  lazy val Altitude  : Double  = coordinates(2)

  override def toString() = s"{ 'longitude':'${Longitude}', 'latitude':'${Latitude}', 'altitude':'${Altitude}' }";

}
