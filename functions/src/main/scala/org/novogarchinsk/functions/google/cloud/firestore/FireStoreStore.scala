package org.novogarchinsk.functions.google.cloud.firestore

import io.scalajs.npm.express.{Request, Response}
import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFirestore.{CollectionReference, DocumentReference, DocumentSnapshot, SetOptions}
import org.novogarchinsk.functions.{Identifier, IdentifierName, StoreableDocument, TimeSeriesStore, TimeValue}
import scalaz.zio.{IO, ZIO}

import scala.compat.Platform
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import scala.scalajs.js
import scala.scalajs.js.Dictionary
import scala.scalajs.js.annotation.JSExportTopLevel

class Firestore {

}
object gcloud {

  //ToDo:: how to limit the id
  type StringID = String //Refined MinSize[Witness.`5`.T] And MaxSize[Witness.`15`.T]

  class GCFireStore[StoredDocumentT:StoreableDocument](
                                                        conv: (DocumentSnapshot => IO[Throwable,StoredDocumentT])
                                                      ) extends TimeSeriesStore[StoredDocumentT,StringID, Throwable, FirebaseAdmin.FireStoreDB, ZIO] {

    type Env = FirebaseAdmin.FireStoreDB

    implicit val identifierName: IdentifierName[StoredDocumentT,StringID] = new IdentifierName[StoredDocumentT,StringID]("")

    override def getById(identifier: Identifier[StoredDocumentT,StringID]): ZIO[Env, Throwable, StoredDocumentT] = for {
      db <- ZIO.environment[Env]
      a <- ZIO.fromFuture(implicit ex =>
        db.doc(identifier.value.toString).get().toFuture)
      b <-  conv(a)
    } yield b

    override def store(value: StoredDocumentT) = ???
  }

  implicit val storedDocument : StoreableDocument[TimeValue] = new StoreableDocument[TimeValue] {
    override def identifier[With](d: TimeValue)(implicit c: TimeValue => With) =  new Identifier[TimeValue,With](c(d)) {}

    override def rowname = ???
  }

  implicit class UnionFold[A, B](union: js.|[A, B]) {
    def fold[X](fa: A => X, fb: B => X)(implicit classTagA: ClassTag[A], classTagB: ClassTag[B]): X = (union: Any) match {
      case a: A => fa(a)
      case b: B => fb(b)
    }
  }

  object TimeSeriesStore extends GCFireStore[TimeValue](documentSnapshot =>  {
    ZIO.apply( TimeValue(
      Integer.parseInt(documentSnapshot.id),
      documentSnapshot.data().fold(fa => fa.get(storedDocument.rowname).fold( 2.0)(_ => 2.0) ,_ => 0)
    ))
  })
}







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










