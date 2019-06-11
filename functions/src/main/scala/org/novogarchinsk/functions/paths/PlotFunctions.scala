package org.novogarchinsk.functions.paths

import org.novogarchinsk.functions.TimeValue
import org.novogarchinsk.functions.paths.jsimport.PathsStock
import org.novogarchinsk.functions.paths.jsimport.PathsStock.StockOpts
import slinky.core.facade.ReactElement
import slinky.web.html.{body, html}
import slinky.web.svg._

import scala.language.{higherKinds, implicitConversions}
import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object PlotFunctions {

  def drawTimeSeriesAsStockChartSVG(vals : Seq[TimeValue]) : ReactElement={
    val stock = PathsStock.apply(new StockOpts[TimeValue] {
      override val data = Seq(vals.toJSArray).toJSArray
      override val xaccessor = js.UndefOr.any2undefOrA(_.time)
      override val yaccessor = js.UndefOr.any2undefOrA(_.value)
      override val width = 420
      override val height = 360
      override val closed = true
    })
      .curves map { curve =>
      g(
        path(d := curve.area.path.print(), fill := "black", stroke := "none"),
        path(d := curve.line.path.print(), fill := "none", stroke := "black"))
    }
    svg(width := "480", height := "400")(stock.jsIterator().next().value)
  }

  def drawElementAsHtmlPage(reactElement: ReactElement) : String={
    slinky.web.ReactDOMServer.renderToStaticMarkup(html(body(reactElement)))
  }


}
