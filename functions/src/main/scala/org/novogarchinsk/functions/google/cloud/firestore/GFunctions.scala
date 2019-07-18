package org.novogarchinsk.functions.google.cloud.firestore

import io.scalajs.npm.express.{Request, Response}
import org.novogarchinsk.functions.TimeValue
import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFirestore.{CollectionReference, DocumentReference, SetOptions}

import scala.compat.Platform
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.JSExportTopLevel


object TimeSeriesStoreFunctions {

  implicit val ec = ExecutionContext.global

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
