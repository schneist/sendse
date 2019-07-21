
import org.scalatest.WordSpec

import scala.language.{higherKinds, implicitConversions, postfixOps}
import scala.scalajs.js.Any

class StoreTest extends WordSpec  {

  import org.novogarchinsk.functions.google.cloud.firestore.gcloud
  case class Address(street: String, zip: String)


  val mp = Map[String,String](
    "street" -> "Tom",
    "zip" -> "asdfa"
  )
  import scalajs.js.JSConverters._
  val dict : scala.scalajs.js.Dictionary[scala.scalajs.js.Any]= mp.mapValues[Any](Any.fromString).toJSDictionary
//  println(dict)
  val ll = gcloud.to[Address].from(dict)
println("------" +ll )

}

