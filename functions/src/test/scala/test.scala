
import colors.Color
import org.scalatest.WordSpec
import paths.high.Stock
import slinky.core.WithAttrs
import slinky.web.ReactDOMServer
import slinky.web.svg._

import scala.language.{higherKinds, implicitConversions, postfixOps}
import scala.scalajs.js

class RenderTest extends WordSpec  {



  val tickers = List(
    List(
      Event("Jan 2000", 39.81),
      Event("Feb 2000", 36.35),
      Event("Mar 2000", 43.22),
      Event("Apr 2000", 28.37),
      Event("May 2000", 25.45),
      Event("Jun 2000", 32.54),
      Event("Jul 2000", 28.4),
      Event("Aug 2000", 28.4),
      Event("Sep 2000", 24.53),
      Event("Oct 2000", 28.02),
      Event("Nov 2000", 23.34),
      Event("Dec 2000", 17.65),
      Event("Jan 2001", 24.84),
      Event("Feb 2001", 24.0),
      Event("Mar 2001", 22.25),
      Event("Apr 2001", 27.56),
      Event("May 2001", 28.14),
      Event("Jun 2001", 29.7),
      Event("Jul 2001", 26.93),
      Event("Aug 2001", 23.21),
      Event("Sep 2001", 20.82),
      Event("Oct 2001", 23.65),
      Event("Nov 2001", 26.12),
      Event("Dec 2001", 26.95),
      Event("Jan 2002", 25.92),
      Event("Feb 2002", 23.73),
      Event("Mar 2002", 24.53),
      Event("Apr 2002", 21.26),
      Event("May 2002", 20.71),
      Event("Jun 2002", 22.25),
      Event("Jul 2002", 19.52),
      Event("Aug 2002", 19.97),
      Event("Sep 2002", 17.79),
      Event("Oct 2002", 21.75),
      Event("Nov 2002", 23.46),
      Event("Dec 2002", 21.03),
      Event("Jan 2003", 19.31),
      Event("Feb 2003", 19.34),
      Event("Mar 2003", 19.76),
      Event("Apr 2003", 20.87),
      Event("May 2003", 20.09),
      Event("Jun 2003", 20.93),
      Event("Jul 2003", 21.56),
      Event("Aug 2003", 21.65),
      Event("Sep 2003", 22.69),
      Event("Oct 2003", 21.45),
      Event("Nov 2003", 21.1),
      Event("Dec 2003", 22.46),
      Event("Jan 2004", 22.69),
      Event("Feb 2004", 21.77),
      Event("Mar 2004", 20.46),
      Event("Apr 2004", 21.45),
      Event("May 2004", 21.53),
      Event("Jun 2004", 23.44),
      Event("Jul 2004", 23.38),
      Event("Aug 2004", 22.47),
      Event("Sep 2004", 22.76),
      Event("Oct 2004", 23.02),
      Event("Nov 2004", 24.6),
      Event("Dec 2004", 24.52),
      Event("Jan 2005", 24.11),
      Event("Feb 2005", 23.15),
      Event("Mar 2005", 22.24),
      Event("Apr 2005", 23.28),
      Event("May 2005", 23.82),
      Event("Jun 2005", 22.93),
      Event("Jul 2005", 23.64),
      Event("Aug 2005", 25.35),
      Event("Sep 2005", 23.83),
      Event("Oct 2005", 23.8),
      Event("Nov 2005", 25.71),
      Event("Dec 2005", 24.29),
      Event("Jan 2006", 26.14),
      Event("Feb 2006", 25.04),
      Event("Mar 2006", 25.36),
      Event("Apr 2006", 22.5),
      Event("May 2006", 21.19),
      Event("Jun 2006", 21.8),
      Event("Jul 2006", 22.51),
      Event("Aug 2006", 24.13),
      Event("Sep 2006", 25.68),
      Event("Oct 2006", 26.96),
      Event("Nov 2006", 27.66),
      Event("Dec 2006", 28.13),
      Event("Jan 2007", 29.07),
      Event("Feb 2007", 26.63),
      Event("Mar 2007", 26.35),
      Event("Apr 2007", 28.3),
      Event("May 2007", 29.11),
      Event("Jun 2007", 27.95),
      Event("Jul 2007", 27.5),
      Event("Aug 2007", 27.34),
      Event("Sep 2007", 28.04),
      Event("Oct 2007", 35.03),
      Event("Nov 2007", 32.09),
      Event("Dec 2007", 34.0),
      Event("Jan 2008", 31.13),
      Event("Feb 2008", 26.07),
      Event("Mar 2008", 27.21),
      Event("Apr 2008", 27.34),
      Event("May 2008", 27.25),
      Event("Jun 2008", 26.47),
      Event("Jul 2008", 24.75),
      Event("Aug 2008", 26.36),
      Event("Sep 2008", 25.78),
      Event("Oct 2008", 21.57),
      Event("Nov 2008", 19.66),
      Event("Dec 2008", 18.91)
    ),
    List(
      Event("Jan 2000", 25.94),
      Event("Feb 2000", 28.66),
      Event("Mar 2000", 33.95),
      Event("Apr 2000", 31.01),
      Event("May 2000", 21.0),
      Event("Jun 2000", 26.19),
      Event("Jul 2000", 25.41),
      Event("Aug 2000", 30.47),
      Event("Sep 2000", 12.88),
      Event("Oct 2000", 9.78),
      Event("Nov 2000", 8.25),
      Event("Dec 2000", 7.44),
      Event("Jan 2001", 10.81),
      Event("Feb 2001", 9.12),
      Event("Mar 2001", 11.03),
      Event("Apr 2001", 12.74),
      Event("May 2001", 9.98),
      Event("Jun 2001", 11.62),
      Event("Jul 2001", 9.4),
      Event("Aug 2001", 9.27),
      Event("Sep 2001", 7.76),
      Event("Oct 2001", 8.78),
      Event("Nov 2001", 10.65),
      Event("Dec 2001", 10.95),
      Event("Jan 2002", 12.36),
      Event("Feb 2002", 10.85),
      Event("Mar 2002", 11.84),
      Event("Apr 2002", 12.14),
      Event("May 2002", 11.65),
      Event("Jun 2002", 8.86),
      Event("Jul 2002", 7.63),
      Event("Aug 2002", 7.38),
      Event("Sep 2002", 7.25),
      Event("Oct 2002", 8.03),
      Event("Nov 2002", 7.75),
      Event("Dec 2002", 7.16),
      Event("Jan 2003", 7.18),
      Event("Feb 2003", 7.51),
      Event("Mar 2003", 7.07),
      Event("Apr 2003", 7.11),
      Event("May 2003", 8.98),
      Event("Jun 2003", 9.53),
      Event("Jul 2003", 10.54),
      Event("Aug 2003", 11.31),
      Event("Sep 2003", 10.36),
      Event("Oct 2003", 11.44),
      Event("Nov 2003", 10.45),
      Event("Dec 2003", 10.69),
      Event("Jan 2004", 11.28),
      Event("Feb 2004", 11.96),
      Event("Mar 2004", 13.52),
      Event("Apr 2004", 12.89),
      Event("May 2004", 14.03),
      Event("Jun 2004", 16.27),
      Event("Jul 2004", 16.17),
      Event("Aug 2004", 17.25),
      Event("Sep 2004", 19.38),
      Event("Oct 2004", 26.2),
      Event("Nov 2004", 33.53),
      Event("Dec 2004", 32.2),
      Event("Jan 2005", 38.45),
      Event("Feb 2005", 44.86),
      Event("Mar 2005", 41.67),
      Event("Apr 2005", 36.06),
      Event("May 2005", 39.76),
      Event("Jun 2005", 36.81),
      Event("Jul 2005", 42.65),
      Event("Aug 2005", 46.89),
      Event("Sep 2005", 53.61),
      Event("Oct 2005", 57.59),
      Event("Nov 2005", 67.82),
      Event("Dec 2005", 71.89),
      Event("Jan 2006", 75.51),
      Event("Feb 2006", 68.49),
      Event("Mar 2006", 62.72),
      Event("Apr 2006", 70.39),
      Event("May 2006", 59.77),
      Event("Jun 2006", 57.27),
      Event("Jul 2006", 67.96),
      Event("Aug 2006", 67.85),
      Event("Sep 2006", 76.98),
      Event("Oct 2006", 81.08),
      Event("Nov 2006", 91.66),
      Event("Dec 2006", 84.84),
      Event("Jan 2007", 85.73),
      Event("Feb 2007", 84.61),
      Event("Mar 2007", 92.91),
      Event("Apr 2007", 99.8),
      Event("May 2007", 121.19),
      Event("Jun 2007", 122.04),
      Event("Jul 2007", 131.76),
      Event("Aug 2007", 138.48),
      Event("Sep 2007", 153.47),
      Event("Oct 2007", 189.95),
      Event("Nov 2007", 182.22),
      Event("Dec 2007", 198.08),
      Event("Jan 2008", 135.36),
      Event("Feb 2008", 125.02),
      Event("Mar 2008", 143.5),
      Event("Apr 2008", 173.95),
      Event("May 2008", 188.75),
      Event("Jun 2008", 167.44),
      Event("Jul 2008", 158.95),
      Event("Aug 2008", 169.53),
      Event("Sep 2008", 113.66),
      Event("Oct 2008", 107.59),
      Event("Nov 2008", 92.67),
      Event("Dec 2008", 85.35)
    ),
    List(
      Event("Jan 2000", 64.56),
      Event("Feb 2000", 68.87),
      Event("Mar 2000", 67.0),
      Event("Apr 2000", 55.19),
      Event("May 2000", 48.31),
      Event("Jun 2000", 36.31),
      Event("Jul 2000", 30.12),
      Event("Aug 2000", 41.5),
      Event("Sep 2000", 38.44),
      Event("Oct 2000", 36.62),
      Event("Nov 2000", 24.69),
      Event("Dec 2000", 15.56),
      Event("Jan 2001", 17.31),
      Event("Feb 2001", 10.19),
      Event("Mar 2001", 10.23),
      Event("Apr 2001", 15.78),
      Event("May 2001", 16.69),
      Event("Jun 2001", 14.15),
      Event("Jul 2001", 12.49),
      Event("Aug 2001", 8.94),
      Event("Sep 2001", 5.97),
      Event("Oct 2001", 6.98),
      Event("Nov 2001", 11.32),
      Event("Dec 2001", 10.82),
      Event("Jan 2002", 14.19),
      Event("Feb 2002", 14.1),
      Event("Mar 2002", 14.3),
      Event("Apr 2002", 16.69),
      Event("May 2002", 18.23),
      Event("Jun 2002", 16.25),
      Event("Jul 2002", 14.45),
      Event("Aug 2002", 14.94),
      Event("Sep 2002", 15.93),
      Event("Oct 2002", 19.36),
      Event("Nov 2002", 23.35),
      Event("Dec 2002", 18.89),
      Event("Jan 2003", 21.85),
      Event("Feb 2003", 22.01),
      Event("Mar 2003", 26.03),
      Event("Apr 2003", 28.69),
      Event("May 2003", 35.89),
      Event("Jun 2003", 36.32),
      Event("Jul 2003", 41.64),
      Event("Aug 2003", 46.32),
      Event("Sep 2003", 48.43),
      Event("Oct 2003", 54.43),
      Event("Nov 2003", 53.97),
      Event("Dec 2003", 52.62),
      Event("Jan 2004", 50.4),
      Event("Feb 2004", 43.01),
      Event("Mar 2004", 43.28),
      Event("Apr 2004", 43.6),
      Event("May 2004", 48.5),
      Event("Jun 2004", 54.4),
      Event("Jul 2004", 38.92),
      Event("Aug 2004", 38.14),
      Event("Sep 2004", 40.86),
      Event("Oct 2004", 34.13),
      Event("Nov 2004", 39.68),
      Event("Dec 2004", 44.29),
      Event("Jan 2005", 43.22),
      Event("Feb 2005", 35.18),
      Event("Mar 2005", 34.27),
      Event("Apr 2005", 32.36),
      Event("May 2005", 35.51),
      Event("Jun 2005", 33.09),
      Event("Jul 2005", 45.15),
      Event("Aug 2005", 42.7),
      Event("Sep 2005", 45.3),
      Event("Oct 2005", 39.86),
      Event("Nov 2005", 48.46),
      Event("Dec 2005", 47.15),
      Event("Jan 2006", 44.82),
      Event("Feb 2006", 37.44),
      Event("Mar 2006", 36.53),
      Event("Apr 2006", 35.21),
      Event("May 2006", 34.61),
      Event("Jun 2006", 38.68),
      Event("Jul 2006", 26.89),
      Event("Aug 2006", 30.83),
      Event("Sep 2006", 32.12),
      Event("Oct 2006", 38.09),
      Event("Nov 2006", 40.34),
      Event("Dec 2006", 39.46),
      Event("Jan 2007", 37.67),
      Event("Feb 2007", 39.14),
      Event("Mar 2007", 39.79),
      Event("Apr 2007", 61.33),
      Event("May 2007", 69.14),
      Event("Jun 2007", 68.41),
      Event("Jul 2007", 78.54),
      Event("Aug 2007", 79.91),
      Event("Sep 2007", 93.15),
      Event("Oct 2007", 89.15),
      Event("Nov 2007", 90.56),
      Event("Dec 2007", 92.64),
      Event("Jan 2008", 77.7),
      Event("Feb 2008", 64.47),
      Event("Mar 2008", 71.3),
      Event("Apr 2008", 78.63),
      Event("May 2008", 81.62),
      Event("Jun 2008", 73.33),
      Event("Jul 2008", 76.34),
      Event("Aug 2008", 80.81),
      Event("Sep 2008", 72.76),
      Event("Oct 2008", 57.24),
      Event("Nov 2008", 42.7),
      Event("Dec 2008", 51.28)
    )
  )


  case class Event(date: String, value: Double)

  private val palette = colors.mix(Color(130, 140, 210), Color(180, 205, 150))
  private val months = List("Jan", "Feb", "Mar", "Apr", "May",
    "Jun", "Jul","Aug", "Sep", "Oct", "Nov", "Dec")

  private def parseDate(event: Event) = {
    val date = new js.Date
    val Array(month, year) = event.date split ' '
    date.setFullYear(year.toInt)
    date.setMonth(months.indexOf(month))

    date.getTime
  }
  "The PureCompiler" should {

    "translate Map" in {
      val stock = Stock.apply[Event](
        data = tickers,
        xaccessor = parseDate,
        yaccessor = _.value,
        width = 420,
        height = 360,
        closed = true
      )
      val lines :js.Array[WithAttrs[g.tag.type]] = stock.curves map { curve =>
        g(
          transform := "translate(50,0)",
          path(d := curve.area.path.print(), fill := "black", stroke := "none"),
          path(d := curve.line.path.print(), fill := "none", stroke := "black"))

      }

      val svgString = svg(width := "480", height := "400")(   lines.jsIterator().next().value  )
      println(" -----" + ReactDOMServer.renderToStaticMarkup(svgString))
    }
  }
}
object colors {
  case class Color(r: Double, g: Double, b: Double, alpha: Double = 1)

  def cut(x: Double) = x.floor min 255

  def multiply(factor: Double) = { c: Color =>
    Color(cut(factor * c.r), cut(factor * c.g), cut(factor * c.b), c.alpha)
  }

  def average(c1: Color, c2: Color) =
    Color(
      cut((c1.r + c2.r) / 2),
      cut((c1.g + c2.g) / 2),
      cut((c1.b + c2.b) / 2),
      (c1.alpha + c2.alpha / 2)
    )

  val lighten = multiply(1.2)
  val darken = multiply(0.8)

  def mix(c1: Color, c2: Color) = {
    val c3 = average(c1, c2)
    val colors = List(
      lighten(c1),
      c1,
      darken(c1),
      lighten(c3),
      c3,
      darken(c3),
      lighten(c2),
      c2,
      darken(c2)
    )

    Stream.continually(colors).flatten
  }

  def transparent(c: Color, alpha: Double = 0.7) = c.copy(alpha = alpha)

  def string(c: Color) =
    if (c.alpha == 1) s"rgb(${ c.r.floor },${ c.g.floor },${ c.b.floor })"
    else s"rgba(${ c.r.floor },${ c.g.floor },${ c.b.floor },${ c.alpha })"
}