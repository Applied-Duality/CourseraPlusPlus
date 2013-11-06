package suggestions



import scala.util.{Try, Success, Failure}
import rx.lang.scala._
import org.scalatest._
import gui._



class WikipediaApiTest extends FlatSpec {

  object mockApi extends WikipediaApi {
    def wikipediaSuggestion(term: String) = ???
    def wikipediaPage(term: String) = ???
  }

  "WikipediaApi" should "make the stream valid" in {
    val invalid = Observable("erik", "erik meijer", "martin")
    val valid = mockApi.validStream(invalid)
    val sub = valid subscribe {
      term => assert(term.forall(_ != ' '))
    }
  }

  // THE REST OF THE TESTS ARE NOT VISIBLE TO THE STUDENTS

  it should "wrap the stream value in a Try" in {
    val invalid = Observable("erik", "erik meijer", "martin")
    val errored = invalid.map(term => if (term.exists(_ == ' ')) throw new IllegalArgumentException(term) else term)
    val validated = mockApi.tryStream(errored)
    val sub = validated.toSeq subscribe {
      terms => assert(terms == Seq(Success("erik"), Failure(new IllegalArgumentException("erik meijer"))))
    }
  }

  it should "correctly compose the response streams" in {
    // TODO
  }

}
