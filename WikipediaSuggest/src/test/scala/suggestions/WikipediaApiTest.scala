package suggestions



import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}
import rx.lang.scala._
import org.scalatest._
import gui._



class WikipediaApiTest extends FlatSpec {

  object mockApi extends WikipediaApi {
    def wikipediaSuggestion(term: String) = Future {
      if (term.head.isLetter) {
        for (suffix <- List(" (Computer Scientist)", " (Footballer)")) yield term + suffix
      } else {
        List(term)
      }
    }
    def wikipediaPage(term: String) = Future {
      "Title: " + term
    }
  }

  "WikipediaApi" should "make the stream valid using validStream" in {
    val notvalid = Observable("erik", "erik meijer", "martin")
    val valid = mockApi.validStream(notvalid)

    var count = 0
    var completed = false
    
    val sub = valid.subscribe(
      term => {
        assert(term.forall(_ != ' '))
        count += 1
      },
      t => assert(false, s"stream error $t"),
      () => completed = true
    )
    assert(completed && count == 3)
  }

  it should "correctly calculate the sum using responseStream" in {
    val requests = Observable(1, 2, 3)
    val remoteComputation = (n: Int) => Observable(0 to n)
    val responses = mockApi.responseStream(requests, remoteComputation)
    val sum = responses.foldLeft(0) { (acc, tn) =>
      tn match {
        case Success(n) => acc + n
        case Failure(t) => throw t
      }
    }
    var total = -1
    val sub = sum.subscribe {
      s => total = s
    }
    assert(total == (1 + 1 + 2 + 1 + 2 + 3), s"Sum: $total")
  }

  // THE REST OF THE TESTS ARE NOT VISIBLE TO THE STUDENTS

  case class WhitespaceException(msg: String) extends Exception

  it should "wrap the stream value in a Try using tryStream" in {
    val notvalid = Observable("erik", "erik meijer", "martin")
    val errored = notvalid.map(term => if (term.exists(_ == ' ')) throw new WhitespaceException(term) else term)
    val validated = mockApi.tryStream(errored)

    var values: Seq[Try[String]] = Nil

    val sub = validated.toSeq.subscribe(
      terms => values = terms,
      t => assert(false, s"stream error $t")
    )
    assert(values == Seq(Success("erik"), Failure(new WhitespaceException("erik meijer"))), s"values: $values")
  }

  it should "correctly compose the streams that have errors using responseStream" in {
    val scientists = Observable("erik", "erik meijer", "martin")
    val remoteComputation = (term: String) => {
      if (term.exists(_ == ' ')) Observable(new WhitespaceException(term))
      else Observable(term + " (Computer Scientist)")
    }
    val responses = mockApi.responseStream(scientists, remoteComputation)

    var all: Set[Try[String]] = Set()
    val sub = responses.toSeq.subscribe {
      s => all = s.toSet
    }
    assert(all == Set(
      Success("erik (Computer Scientist)"),
      Failure(new WhitespaceException("erik meijer")),
      Success("martin (Computer Scientist)")
    ), all)
  }

  it should "return a stream of suggestions, with a single value and completed" in {
    val suggestions = mockApi.wikiSuggestResponseStream("Alan Turing")

    @volatile var completed = false
    @volatile var error = false
    @volatile var result: List[String] = Nil

    val sub = suggestions.subscribe(
      s => result = s,
      t => error = true,
      () => completed = true
    )

    Thread.sleep(1000)

    assert(completed, "stream not completed")
    assert(!error, "error occurred")
    assert(result == List("Alan Turing (Computer Scientist)", "Alan Turing (Footballer)"), "result was: " + result)
  }

  it should "return a failed stream of suggestions" in {
    val suggestions = mockApi.wikiSuggestResponseStream("")

    @volatile var completed = false
    @volatile var error: Throwable = null
    @volatile var result: List[String] = Nil

    val sub = suggestions.subscribe(
      s => result = s,
      t => error = t,
      () => completed = true
    )

    Thread.sleep(1000)

    assert(!completed, "stream not completed")
    assert(error.isInstanceOf[NoSuchElementException], "error occurred: " + error)
    assert(result == Nil, "result was: " + result)
  }

}
