package net.novogarchinsk

import monix.eval.Task
import monix.execution.Cancelable
import monix.execution.Scheduler.Implicits.global
import slinky.core._
import slinky.core.annotations.react
import slinky.native._

import scala.scalajs.js
import scala.scalajs.js.Dynamic.literal
import scala.scalajs.js.UndefOr
import scala.util.Try


@react
class App extends Component {
  type Props = Unit




  case class State(loca :String,series:Map[String,String])

  override  def initialState = State("",Map.empty)

  def getTS() ={


  }

  def render() = {
    View(
      style = literal(
        padding = 50,
        flex = 1,
        flexDirection = "column",
        justifyContent = "center",
        alignItems = "center"
      )
    )(
      WebView(source =  source("https://www.google.de"),
        style = literal(
        width = 250,
        height = 250
        )
      ),

      Button(title = "Refresh!", onPress = () => {
        getLocation().runAsync(e=> e.fold((l => setState(s => State(l.toString,s.series))),(l => setState(s => State(l,s.series)))))
      }),
      /**
      MapView(style = literal(
        width = 250,
        height = 250
      ),region = region(1.0,1.0,1.0,1.0)
      ),
        */
    )
  }

  def getLocation(): Task[String ] = Task.create { (_, callback) => {
    val pf :(js.Dynamic) => Unit = (s :js.Dynamic) => callback.apply(Right(s.coords.latitude.toString()))

    Try{
      js.Dynamic.global.navigator.geolocation.requestAuthorization()
      js.Dynamic.global.navigator.geolocation.getCurrentPosition(pf)
    }.fold(callback.onError _, (callback.onSuccess _ ).compose(_.toString ))
    Cancelable.empty
  }}
}