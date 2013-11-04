import observablex.Scheduler
import scala.language.postfixOps
import search.Search

object WikipediaSuggest {
  def main(args: Array[String]): Unit = {

    implicit val s = Scheduler.NewThreadScheduler

    Search.quakesWithCountry().subscribe(onNext = x => println(x))

    readLine()
    println("bye")

  }
}




