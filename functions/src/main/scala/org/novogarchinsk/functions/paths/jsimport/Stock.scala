package org.novogarchinsk.functions.paths.jsimport

import org.novogarchinsk.functions.paths.jsimport.Path.Path
import org.novogarchinsk.functions.paths.jsimport.PathsLinear.Linear
import org.novogarchinsk.functions.paths.jsimport.PathsPolygon.{Polygon, PolygonOpts}
import org.novogarchinsk.functions.paths.jsimport.paths.Point

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSImport



@js.native
@JSImport("paths-js/path", JSImport.Namespace)
object Path extends js.Object {
  def apply(): Path = js.native


  @js.native
  trait Path extends js.Object {
    def print(): String = js.native
    def points(): js.Array[Point] = js.native
    def connect(path: Path): Path = js.native

    def moveto(x: Number, y: Number): Path = js.native
    def lineto(x: Number, y: Number): Path = js.native
    def hlineto(x: Number): Path = js.native
    def vlineto(y: Number): Path = js.native
    def curveto(x1: Number, y1: Number, x2: Number, y2: Number, x: Number, y: Number): Path = js.native
    def smoothcurveto(x2: Number, y2: Number, x: Number, y: Number): Path = js.native
    def qcurveto(x1: Number, y1: Number, x: Number, y: Number): Path = js.native
    def smoothqcurveto(x: Number, y: Number): Path = js.native
    def arc(rx: Number, ry: Number, xrot: Number, largeArcFlag: Number, sweepFlag: Number, x: Number, y: Number): Path = js.native
    def closepath(): Path = js.native
  }
}


@js.native
@JSImport("paths-js/polygon", JSImport.Namespace)
object PathsPolygon extends js.Object {
  def apply(options: PolygonOpts): Polygon = js.native


  @js.native
  trait Shape extends js.Object {
    val path: Path = js.native
    val centroid: Point = js.native
  }

  @js.native
  trait Polygon extends Shape




  @js.native
  trait PolygonOpts extends js.Object {
    val points: js.Array[Point] = js.native
    val closed: Boolean = js.native
  }

}


object PolygonOpts {
  def apply(points: js.Array[Point], closed: Boolean = false): PolygonOpts =
    js.Dynamic.literal(
      points = points,
      closed = closed
    ).asInstanceOf[PolygonOpts]
}

object Polygon {
  def apply(points: Seq[(Double, Double)], closed: Boolean = false): Polygon = {
    val jsPoints = for ((x, y) <- points.toJSArray) yield js.Array(x, y)
    PathsPolygon(PolygonOpts(jsPoints, closed))
  }
}

@js.native
@JSImport("paths-js/linear", JSImport.Namespace)
object PathsLinear extends js.Object {
  def apply(source: js.Array[Double], target: js.Array[Double]): Linear = js.native

  @js.native
  trait Linear extends js.Object {
    def apply(x: Double): Double = js.native

    def inverse(): Linear = js.native
  }



}

object Linear {
  def apply(source: (Double, Double), target: (Double, Double)): Linear = {
    val (a, b) = source
    val (c, d) = target
    PathsLinear(js.Array(a, b), js.Array(c, d))
  }
}

package object paths {
  type Point = js.Array[Double]
  private[paths] def tuple2point(x: (Double, Double)) = js.Array(x._1, x._2)
}


@js.native
@JSImport("paths-js/stock", JSImport.Namespace)
object PathsStock extends js.Object {

  def apply[A](option: StockOpts[A] ) :Stock[A] = js.native

  trait StockOpts[A] extends js.Object {
    val data: js.UndefOr[js.Array[js.Array[A]]] = js.undefined
    val xaccessor: js.UndefOr[js.Function1[A, Double]] = js.undefined
    val yaccessor: js.UndefOr[js.Function1[A, Double]] = js.undefined
    val width: js.UndefOr[Int] = js.undefined
    val height: js.UndefOr[Int] = js.undefined
    val closed: js.UndefOr[Boolean] = js.undefined
    val sort: js.UndefOr[Boolean] = js.undefined
  }


  @js.native
  trait StockCurve[A] extends js.Object {
    val line: Polygon = js.native
    val area: Polygon = js.native
    val item: js.Array[A] = js.native
    val index: Int = js.native
  }

  @js.native
  class Stock[A] extends js.Object {
    val curves: js.Array[StockCurve[A]] = js.native
    val xscale: Linear = js.native
    val yscale: Linear = js.native
  }


}