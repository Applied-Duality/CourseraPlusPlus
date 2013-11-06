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
This is a common pattern when bridging between callback-based and reactive stream-based systems.

Your task is to implement the following methods in `WikipediaSuggest` by using the `Observable.apply(f: Observer => Subscription)` factory method:

    def textFieldValues(field: TextField): Observable[String]

    def buttonClicks(button: Button): Observable[AbstractButton]

Scala Swing components can be `subscribe`d to by creating `Reaction` objects.
These `Reaction` objects in essence wrap `PartialFunction`s that handle [Scala Swing events](http://www.scala-lang.org/api/current/index.html#scala.swing.event.package)
that correspond to the component the `Reaction` is used for.

Similarly, implement the method `apply` in `ObservableEx` that converts a future into an `Observable`:

    def apply[T](f: Future[T])(implicit execContext: ExecutionContext): Observable[T]

Note: use the [`AsyncSubject`](http://netflix.github.io/RxJava/javadoc/rx/subjects/AsyncSubject.html) above.

Be sure to understand the [`Observable`](http://netflix.github.io/RxJava/javadoc/) [contract](https://github.com/Netflix/RxJava/wiki/Observable) before you begin.


## Wikipedia suggestion utilities

Creating `Observable`s manually is generally discouraged.
Instead, you should use combinators on `Observable`s wherever possible to compose them into more complex ones.
In the last part of the exercise you implemented `Observable`s over different input events.
In this part you will implement some utility functions over `Observable`s that will help you complete the GUI functionality in the final part.


## Putting it all together

TODO completing the main application.
