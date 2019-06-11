package org.novogarchinsk.functions

import io.scalajs.npm.express.{Request, Response}
import org.novogarchinsk.functions.paths.PlotFunctions.{drawElementAsHtmlPage, drawTimeSeriesAsStockChartSVG}
import org.novogarchinsk.functions.google.cloud.firestore.TimeSeriesStoreFunctions
import org.novogarchinsk.functions.google.cloud.firestore.TimeSeriesStoreFunctions.db

import scala.concurrent.ExecutionContext
import scala.scalajs.js.annotation.JSExportTopLevel

object HTMLEndpoints {

  implicit val ex = ExecutionContext.global

  @JSExportTopLevel("renderSVG")
  def drawTimeSeriesAsPage(req: Request, res: Response) = {
    TimeSeriesStoreFunctions.extractTimeSeries(db.collection("imsi").doc(req.param("imsi","invalid")))
      .map(drawTimeSeriesAsStockChartSVG)
      .map(drawElementAsHtmlPage)
      .map(svgString => res.send(svgString))
  }

}
