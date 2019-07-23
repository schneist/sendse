
import org.scalatest.WordSpec

import scala.language.{higherKinds, implicitConversions, postfixOps}
import scala.scalajs.js.Any
import scala.scalajs.js.JSConverters._

class StoreTest extends WordSpec  {

  import org.novogarchinsk.functions.google.cloud.firestore.gcloud
  case class Street(name:String)
  case class Address( zip: Int,street: Street)


  val mp = Map[String,Any](
    "street" -> Map[String,Any]("name" -> "Tom").toJSDictionary,
    "zip" -> 5
  )

  val dict : scala.scalajs.js.Dictionary[scala.scalajs.js.Any]= mp.toJSDictionary
  val ll = gcloud.to[Address].from(dict)
  println("------" +ll )

}

