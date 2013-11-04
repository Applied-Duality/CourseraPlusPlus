package gui

import observablex.Subscription
import rx.lang.scala._
import scala.collection.mutable.ListBuffer
import scala.swing.event.{ValueChanged, ButtonClicked}
import scala.swing._
import rx.subscriptions._
import swing.Swing._
import Orientation._

object Swing extends SimpleSwingApplication {

  def top = new MainFrame {

    title = "Count to 10"

    val button = new Button("click me")
    val label = new Label("...")
    val text = new TextField(columns = 40)

    val data = ListBuffer[String]()
    val list = new ListView(data)

    contents = new BoxPanel(orientation = Vertical) {
      border = EmptyBorder(top = 30,left = 30,bottom = 30,right = 30)
      contents += text
      contents += button
      contents += label
      contents += new ScrollPane(list)
    }

    var clicks = 0

    val clickStream: Observable[AbstractButton] = Observable(observer => {
      val onClicked = Reaction {
        case clicked: ButtonClicked => { observer.onNext(clicked.source) }
        case _                      => {}
      }
      button.subscribe(onClicked)
      Subscription { button.unsubscribe(onClicked) }
    })

    val textStream: Observable[String] = Observable(observer => {
      val onChanged = Reaction {
        case value: ValueChanged => {
          val s = value.source.asInstanceOf[TextField].text
          observer.onNext(s)
        }
        case _                => {}
      }
      text.subscribe(onChanged)
      Subscription { text.unsubscribe(onChanged) }
    })

    textStream.subscribe(x => {
      data += x
      list.listData = data
    })

    var subscription = new CompositeSubscription()

    subscription.add(clickStream.subscribe(b => {
      clicks += 1
      label.text = s"clicked ${ clicks }"
      if(clicks > 10) {
        button.text = "I'm done"
        subscription.unsubscribe()
      }
    }))

  }
}
