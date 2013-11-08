import bindings.{FeatureCollection, EarthQuake}
import com.google.gson.{JsonElement, JsonObject, Gson}
import javafx.beans.value.ObservableBooleanValue
import observablex.Scheduler
import retrofit.client.Response
import retrofit.{RetrofitError, Callback, RestAdapter}
import rx.lang.scala.Observable
import search._

object Main {

  def main(args: Array[String]): Unit = {

   implicit val s = Scheduler.NewThreadScheduler

    val restAdapter = new RestAdapter.Builder()
      .setServer("http://earthquake.usgs.gov")
      .build()

    val usgs = restAdapter.create(classOf[USGS])

   usgs.q(new Callback[FeatureCollection](){
     def failure(error: RetrofitError): Unit = {
       println("oops"+ error.getResponse.getBody.in().toString)
     }

     def success(t: FeatureCollection, response: Response): Unit = {
      t.features.map(f => println(f))
     }
   })

    //gui.Swing.startup(args)

    //readLine()
    println("bye")


  }
}
