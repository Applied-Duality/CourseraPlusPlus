import scala.concurrent.ExecutionContext.Implicits.global
import observablex._
import search.Search

object Main {

  def main(args: Array[String]): Unit = {
    implicit val s = Scheduler.NewThreadScheduler

    ObservableEx(Search.wikipedia("internet")).subscribe(onNext = x => println(x))

    //gui.Swing.startup(args)

    readLine()
    println("bye")
  }
}
