package search

import retrofit.http.GET
import retrofit.Callback
import bindings.FeatureCollection

trait USGS {
  @GET("/earthquakes/feed/geojson/all/day")
  def q(f: Callback[FeatureCollection])
}
