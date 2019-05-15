package org.novogarchinsk.functions

import io.scalajs.npm.express.{Request, Response}
import org.novogarchinsk.functions.TimeSeriesStoreFunctions.{TimeValue, db, extractTimeSeries}
import org.novogarchinsk.functions.jsimport.PathsStock
import org.novogarchinsk.functions.jsimport.PathsStock.StockOpts
import slinky.core.WithAttrs
import slinky.web.html.{body, html}
import slinky.web.svg.{d, fill, g, height, path, stroke, svg, width}

import scala.concurrent.ExecutionContext
import scala.scalajs.js.JSConverters._
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

object PlotFunctions {

  implicit val ex = ExecutionContext.global

  @JSExportTopLevel("renderSVG")
  def renderSVG(req: Request, res: Response) = {




    extractTimeSeries(db
      .collection("imsi")
      .doc(req.param("imsi","invalid"))).map(v2 => {

      val stock = PathsStock.apply(new StockOpts[TimeValue] {
        override val data = Seq(v2.toJSArray).toJSArray
        override val xaccessor = js.UndefOr.any2undefOrA(_.time)
        override val yaccessor = js.UndefOr.any2undefOrA(_.value)
        override val width = 420
        override val height = 360
        override val closed = true
      })
      val lines: js.Array[WithAttrs[g.tag.type]] = stock.curves map { curve =>
        g(
          //transform := "translate(50,0)",
          path(d := curve.area.path.print(), fill := "black", stroke := "none"),
          path(d := curve.line.path.print(), fill := "none", stroke := "black"))

      }
      val svgString = svg(width := "480", height := "400")(lines.jsIterator().next().value)
      res.send(slinky.web.ReactDOMServer.renderToStaticMarkup(html(body(svgString))))
    })
  }



}
