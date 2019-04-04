package net.novogarchinsk

import com.highcharts.Highcharts
import slinky.hot
import slinky.native.AppRegistry

import scala.scalajs.LinkingInfo
import scala.scalajs.js.annotation.JSExportTopLevel

object Main {
  @JSExportTopLevel("entrypoint.main")
  def main(): Unit = {
    if (LinkingInfo.developmentMode) {
      hot.initialize()
      App.componentConstructor // required to force proxy update
    }

    AppRegistry.registerComponent("frontend", () => {
      App
    })
  }


  def dc ={

    Highcharts.chart()
  }
}
