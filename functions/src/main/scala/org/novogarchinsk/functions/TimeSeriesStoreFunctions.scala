package org.novogarchinsk.functions

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.{MaxSize, MinSize}
import io.scalajs.npm.express.{Request, Response}
import org.novogarchinsk.functions.jsimport.FirebaseFirestore._
import org.novogarchinsk.functions.jsimport.{FirebaseAdmin, FirebaseFunctions}
import scalaz.zio.ZIO
import shapeless.Witness
import scala.language.higherKinds

import scala.compat.Platform
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation._


trait Identifier{
  val name : String Refined MinSize[Witness.`5`.T]  And MaxSize[Witness.`15`.T]
  val value : String Refined MinSize[Witness.`1`.T]
}

sealed trait Limit{

}

trait Stored[V]{

  def id : Identifier

  def get[T[_]](): T[V]

}

trait ERR extends Throwable

class IOERR extends ERR

trait TimeSeriesStore[V <: Stored[V] ,E <: ERR,EE, T[_,_,_],L<:Limit] {

  def getById(identifier:Identifier):T[EE,E,V]

  def getRange(limit: L): T[EE,E,Vector[V]]

  def store(value:V):T[EE,E,Unit]

}

class GCFireStore[Val <: Stored[Val],Lim <: Limit](implicit conv: (DocumentSnapshot => Val) ) extends TimeSeriesStore[Val,Throwable, FirebaseAdmin.FireStoreDB,ZIO,Lim]{

  type Env = FirebaseAdmin.FireStoreDB

  override def getById(identifier: Identifier):ZIO[Env,Throwable,Val] = for{
    db <- ZIO.environment[Env]
    a <- ZIO.fromFuture(ex => {
      db.doc(identifier.value.value).get().toFuture.map(conv)
    })

  } yield a

  override def getRange(limit: Lim) = ???

  override def store(value: Val) = ???
}

object TimeSeriesStoreFunctions {




  FirebaseAdmin.initializeApp(FirebaseFunctions.config().firebase)

  val db: FirebaseAdmin.FireStoreDB = FirebaseAdmin.firestore()

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


}



