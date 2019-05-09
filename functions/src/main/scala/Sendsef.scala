import FirebaseFunctions.FunctionsConfigFirebase
import PathsStock.StockOpts
import functions.FirebaseFirestore._
import io.circe.generic.auto._
import io.scalajs.npm.express.{Request, Response}
import slinky.core.WithAttrs
import slinky.web.html.{body, html}
import slinky.web.svg._

import scala.compat.Platform
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.annotation._
import scala.scalajs.js.{Dictionary, |}
import scala.util.Try
import js.JSConverters._

object Communication{

  sealed trait Command[T]{
    val value:Int
    val t :T
  }

  case class submitValue(value:Int,t:String) extends Command[String]

  sealed trait SensorMode

  trait Threshold[V] extends SensorMode{
    def limit:V
  }

  case class LowerThreshold[V](limit:V) extends Threshold[V]

  case class UpperThreshold[V](limit:V) extends Threshold[V]

  sealed trait SensorType[T]

  case object Luminosity extends SensorType[Double]

  case object Temperature extends  SensorType[Double]

  case class Numbered[T](number :Int) extends SensorType[T]

  sealed trait CommandResponse

  case object OK extends CommandResponse

  case class Error(message:String) extends  CommandResponse

  import io.circe._
  import shapeless.{Coproduct, Generic}

  implicit def encodeAdtNoDiscrT[T,A[T], Repr <: Coproduct](implicit
                                                            gen: Generic.Aux[A[T], Repr],
                                                            encodeRepr: Encoder[Repr]
                                                           ): Encoder[A[T]] = encodeRepr.contramap(gen.to)

  implicit def decodeAdtNoDiscrT[T,A[T], Repr <: Coproduct](implicit
                                                            gen: Generic.Aux[A[T], Repr],
                                                            decodeRepr: Decoder[Repr]
                                                           ): Decoder[A[T]] = decodeRepr.map(gen.from)

  implicit def encodeAdtNoDiscr[A, Repr <: Coproduct](implicit
                                                      gen: Generic.Aux[A, Repr],
                                                      encodeRepr: Encoder[Repr]
                                                     ): Encoder[A] = encodeRepr.contramap(gen.to)

  implicit def decodeAdtNoDiscr[A, Repr <: Coproduct](implicit
                                                      gen: Generic.Aux[A, Repr],
                                                      decodeRepr: Decoder[Repr]
                                                     ): Decoder[A] = decodeRepr.map(gen.from)


}

object Sendsef {








  import Communication._
  import io.circe.parser.decode

  FirebaseAdmin.initializeApp(FirebaseFunctions.config().firebase)

  val db = FirebaseAdmin.firestore()






  trait SubmitValueCO[T] extends js.Object {
    val imsi: js.UndefOr[String] = js.undefined
  }

  def decodePost(req: Request): Either[Throwable, Command[_]] = {
    //val k = encodeAdtNoDiscrT[String,Command[String],_]

    decode[Command[_]] (req.body.toString)
  }

  def decodeGet(req: Request): Either[Throwable, Command[_] ] = {
    Try{
      val commandName = req.params.get("command").getOrElse(req.params.get("commandid"))
      Communication.submitValue
    }.toEither
    ???
  }

  def parse(req:Request) : Either[Throwable, Command[_]] = {
    req.method match {
      case "GET" => decodeGet(req)
      case "POST" => decodePost(req)
      case _ => Left.apply(new Exception("Method not supported: " + req.method ))
    }
  }

  trait simpleSubmit extends js.Object {
    val port: js.UndefOr[ String] = js.undefined
    val value: js.UndefOr[ String] = js.undefined
  }


  @JSExportTopLevel("submitValue")
  def submitValue(req: Request, res: Response) = {
    db
      .collection("imsi")
      .doc(req.param("imsi","invalid"))
      .collection("timeseries")
      .doc(Platform.currentTime.toString)
      .set(
        Dictionary.apply(
          "value" -> req.param("value","--"),
          "port" -> req.param("port","-")
        ),
        new SetOptions {
          override val merge = true
        }
      ).toFuture
      .map(res.send)
  }


  case class TimeValue(time:Long,value:Double)

  def extractTimeSeries(documentReference: DocumentReference):Future[Seq[TimeValue]] = {
    val t :CollectionReference = documentReference
      .collection("timeseries")
    ???
  }


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





  @JSExportTopLevel("getValues")
  def getValue(req: Request, res: Response) = {
    db
      .collection("imsi")
      .doc(req.param("imsi","invalid"))
      .collection("timeseries")
      .get.toFuture.map(d => res.send(d.docs.map(_.data())))
  }




  @JSExportTopLevel("register")
  def register(req: Request, res: Response) = {
    assert(req.param("imsi").isDefined)
    db.collection("ips")
      .doc(req.param("imsi",""))
      .set(Dictionary.apply(
        "ip" -> req.param("ip",req.ip),
        "imsi"-> req.param("imsi","")

      ),
        new SetOptions {
          override val merge = true
        }
      )
    res.send(req.ip.toString)
  }

  @JSExportTopLevel("locationQuery")
  def locationQuery(req: Request, res: Response) = {
    res.send("ohio")
  }

}

@js.native
@JSImport("firebase-functions", JSImport.Namespace)
object FirebaseFunctions extends js.Object {

  def config():FunctionsConfig = js.native


  @js.native
  class  FunctionsConfig extends js.Object {
    def firebase: FunctionsConfigFirebase =  js.native
  }

  @js.native
  class FunctionsConfigFirebase extends js.Object {
  }
}

@js.native
@JSImport("firebase-admin", JSImport.Namespace)
object FirebaseAdmin extends js.Object {

  def initializeApp(functionsConfigFirebase: FunctionsConfigFirebase ) : Unit = js.native

  def firestore(): FireStoreDB = js.native


  @js.native
  class FireStoreDB extends js.Object {
    def collection(name:String) : CollectionReference = js.native
    def doc(name:String): DocumentReference = js.native
  }





  @js.native
  class FSSetwOpt extends js.Object {

  }

}


