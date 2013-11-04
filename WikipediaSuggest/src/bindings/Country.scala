package bindings

import scala.util.Try
import org.json4s.JValue

object Country {
  def apply(raw: JValue): Country = {
    new Country(raw)
  }
}

class Country(raw: JValue) {
  implicit val formats = org.json4s.DefaultFormats

  private def json: JValue  = raw


  lazy val Name: String = { Try((json\"countryName").extract[String]).getOrElse("not found") }
  lazy val Code: String = { Try((json\"countryCode").extract[String]).getOrElse("not found") }
  lazy val Region: String = { Try((json\"adminName1").extract[String]).getOrElse("not found") }


  override def toString(): String = s"{ 'name':'${Name}', 'code':'${Code}', 'region':'${Region}' }";
}
