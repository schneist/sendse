
import eu.timepit.refined.auto._
import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFirestore.DocumentSnapshot
import org.novogarchinsk.functions.google.cloud.firestore.{FirebaseAdmin, FirebaseFirestore, FirebaseFunctions}
import org.novogarchinsk.functions.{Identifier, StoreableDocument}
import org.scalatest.WordSpec
import shapeless.HList
import zio.{DefaultRuntime, ZIO}

import scala.concurrent.ExecutionContext
import scala.language.{higherKinds, implicitConversions, postfixOps}
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{Any, Promise}

class StoreTest extends WordSpec  {
  implicit val ec = ExecutionContext.global
  val rt = new DefaultRuntime {}

  import org.novogarchinsk.functions.google.cloud.firestore.gcloud
  case class Street(name:String)
  case class Address( zip: Int,street: Street)

  FirebaseAdmin.initializeApp(FirebaseFunctions.config().firebase)

  val db: FirebaseFirestore.Firestore = FirebaseAdmin.firestore()

  val mp = Map[String,Any](
    "street" -> Map[String,Any]("name" -> "Tom").toJSDictionary,
    "zip" -> 5
  )
  val add :Promise[FirebaseFirestore.WriteResult] = db.doc("test").set(
      mp.toJSDictionary,new FirebaseFirestore.SetOptions {}
  )
val f = ZIO.fromFuture(implicit ec => add.toFuture.map(_ =>println("------ inserting ")))
  println(rt.unsafeRunSync(f))




/*
  import gcloud._

  implicit val storedDocuments : StoreableDocument[Address] = new StoreableDocument[Address] {
    override def identifier[With](d: Address)(implicit c: Address => With) = new Identifier[Address,With](c(d)) {}

    override def fieldNames() = fieldNames[Address]
  }

  object TestStore extends GCFireStore[Address](conv =  (a:DocumentSnapshot) => {
    a.data().fold(
      aa => ZIO.apply{
        val t :ConvertHelper[Address] = to[Address]
        t.from(aa).get
      }
      ,
      _ => ZIO.fail(new Exception("No data in Snapshot"))
    )
  })
  val ll = gcloud.to[Address].from(mp.toJSDictionary).get

  val id :Identifier[Address,StringID] = new Identifier[Address,StringID]("test")

  val dict = TestStore.getById(id).provide(db)

  println("------" +ll )

  rt.unsafeRunSync(dict)
*/
}

