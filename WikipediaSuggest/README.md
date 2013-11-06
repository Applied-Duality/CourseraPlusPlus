Wikipedia Suggestions
=====================

In this exercise you will develop a reactive graphical user interface for the Wikipedia Suggestions application.
This application will allow the user to enter a search term and in real time render a list of possible completions
of that search term from Wikipedia.
After that, a user will be able to select one of the suggested terms and click `Get`, which will render the desired wikipedia page.


## Observable extensions

Most GUI toolkits are callback-based.
They provide a plethora of different UI components like buttons or text fields, which raise events when something happens to them.
If you click a button, a `ButtonClicked` event is raised and if you type into a text field, a `ValueChanged` event is raised.
To listen to these events, the programmer must install callbacks to corresponding components.
This approach of using callbacks and mutable state for designing large scale applications can quickly lead
to what is known as *the callback hell*, where a programmer can no longer make sense of the code he wrote.

Instead of using callbacks, we would like to handle event streams.
Event streams are first-class values that are handled in a more declarative fashion than callbacks and are more encapsulated.
Event streams can be represented using Rx `Observable`s.
In this part of the exercise you will implement several `Observable`s that emit values whenever a Swing component event is raised.
This is a common pattern when bridging between callback-based and reactive stream-based systems -- remember it well!

Your task is to implement the following methods in `package.scala` by using the `Observable.apply(f: Observer => Subscription)` factory method:

    def textFieldValues(field: TextField): Observable[String] = ???

    def buttonClicks(button: Button): Observable[AbstractButton] = ???

Scala Swing components can be `subscribe`d to by creating `Reaction` objects.
These `Reaction` objects in essence wrap `PartialFunction`s that handle [Scala Swing events](http://www.scala-lang.org/api/current/index.html#scala.swing.event.package)
that correspond to the component the `Reaction` is used for.

The Wikipedia API can give a list of possible completions for a given search term.
As we will see later, it takes HTTP requests and returns responses wrapped in `Future` objects.
Your next task is to implement the general method `apply` in `ObservableEx.scala` that converts any `Future` into an `Observable`:

    def apply[T](f: Future[T])(implicit execContext: ExecutionContext): Observable[T] = ???

Note: use the [`AsyncSubject`](http://netflix.github.io/RxJava/javadoc/rx/subjects/AsyncSubject.html) to do this.

Be sure to understand the [`Observable`](http://netflix.github.io/RxJava/javadoc/) [contract](https://github.com/Netflix/RxJava/wiki/Observable) before you begin.


## Wikipedia suggestion utilities

In the last part of the exercise you implemented `Observable`s over different input events.
In this part you will implement some utility functions over `Observable`s that will help you complete the GUI functionality in the final part.
Creating `Observable`s manually the way we did it in the last part is generally discouraged.
Instead, you should use combinators on `Observable`s wherever possible to compose them into more complex ones.

The Wikipedia API is factored out in the `WikipediaApi` trait.
It contains two abstract methods:

    /** Returns a `Future` with a list of possible completions for a search `term`.
     */
    def wikipediaSuggestion(term: String): Future[List[String]]

    /** Returns a `Future` with the contents of the Wikipedia page for the given search `term`.
     */
    def wikipediaPage(term: String): Future[String]

These methods return futures with a list of possible completions for a search term and the corresponding Wikipedia page, respectively.
However, search terms sent in an HTTP request cannot contain spaces!
Instead, all spaces ` ` in a search term should be replaced with an underscore `_`.
Your task is to implement a method `validStream` in `WikipediaApi.scala` that, given a stream of search terms returns a new stream of search terms such that all
the search terms containing spaces are properly replaced:

    /** Given a stream of search terms, returns a stream of search terms with spaces replaced by underscores.
     *
     *  E.g. `"erik", "erik meijer", "martin` should become `"erik", "erik_meijer", "martin"`
     */
    def validStream(s: Observable[String]): Observable[String] = ???

Use `Observable` [combinator methods](http://netflix.github.io/RxJava/javadoc/rx/Observable.html) to achieve this.

`Observable`s might be completed with errors.
When composing multiple `Observable`s, errors from one of them can easily leak into the resulting `Observable` and complete it by calling `onError`.
To prevent this from happening, any exceptions in the `Observable` should be wrapped into `Failure` objects that can be dealt with as if they
were ordinary values.
Your next task is to implement the method `tryStream` which converts any `Observable[T]` into an `Observable[Try[T]]`:
  
    /** Given an observable that can possibly be completed with an error, returns a new observable
     *  with the same values wrapped into `Success` and the potential error wrapped into `Failure`.
     *  
     *  E.g. `1, 2, 3, !Exception!` should become `Success(1), Success(2), Success(3), Failure(Exception)`
     */
    def tryStream[T](s: Observable[T]): Observable[Try[T]] = ???

Finally, sometimes observables are created from more than just one other observable.
In our case, updates to the list of suggestions depending on the observable of search terms that the user entered
into the search field and the observable of suggestions from Wikipedia for each search term.

Your final task in this part is to implement a method `responseStream` that, given a `requestStream`
and a method to map elements of the request stream (i.e. single requests) into response streams,
returns a single response stream that contains all the responses, both successful and failed,
wrapped into a `Try` object:

    /** Given a stream of requests `requestStream` and a method `requestMethod` to map a request `T` into 
     *  a stream of responses `S`, returns a stream of all the responses wrapped into a `Try`.
     *
     *  E.g. given a request stream:
     *  
     *      1, 2, 3, 4, 5
     *
     *  And a request method:
     *
     *      num => if (num != 4) Observable.just(num) else Observable.error(new Exception)
     *
     *  We should, for example, get:
     *
     *      Success(1), Success(2), Success(3), Failure(new Exception), Success(5)
     *
     */
    def responseStream[T, S](requestStream: Observable[T], requestMethod: T => Observable[S]): Observable[Try[S]] = ???


## Putting it all together

We now have all the ingredients to complete our Wikipedia Suggestions application!
Open `WikipediaSuggest.scala` -- you will see the body of the main Scala Swing based application.
The pieces that concern the static part of the UI are already implemented for you -- your task
is to add some reactive behaviour to this application.

The UI currently contains a text field called `searchTermField`.
Your first task is to construct an observable of text field entries called `searchTerms`:

    val searchTerms: Observable[String] = ???

Next, use the `searchTerms` observable to create an observable of lists of suggestions that where
each list of suggestion corresponds to one search term.
If any of the suggestion lists requests fails, we would like to have the throwable to print the
error message, so we wrap the result into a `Try`.
Use the methods defined earlier in the `WikipediaApi`:

    val suggestions: Observable[Try[List[String]]] = ???

The `suggestions` observable should now be updated while you type.
Problem is -- there is no way to see these changes yet in the UI!
To display them, we need to update the contents of a component called `suggestionList`
every time the observable produces a value.
If the `suggestions` value is not successful, we must print the error message into the `status` label.
Use the `subscribe` method on `suggestions` to do this:

    val suggestionSubscription: Subscription = ???

Our application would be pretty boring if it were only able to give search term suggestions.
We would like to pick one of the search term in the list of suggestions and press `Get` to obtain the
contents of the corresponding Wikipedia page and display it in the panel on the right side of the UI.

Your first task will be to obtain an observable `clicks` of button clicks that
contains the search terms selected in the suggestion list at the time the button was clicked.
If the suggestion list had no items selected, then the click should not be part of `clicks`.

    val clicks: Observable[String] = ???

Next, use the `clicks` observable to obtain an observable of the Wikipedia pages corresponding to
the respective search term (use the previously defined methods from the `WikipediaApi`):

    val pages: Observable[Try[String]] = ???

Again, requests above may fail, so we want to wrap them into `Try`.

Finally, the observable `pages` is of little worth unless its values are rendered somewhere.
Subscribe to the `pages` observable to update the `editorpane` with the contents of the response.

The final application should resemble the following screenshot.
Below you see the Wikipedia results for the entry "Erik Meijer".
It tells us that Erik was previously a 6ft high professional footballer, so you better not mess with him!

![Application screenshot](application.png)






