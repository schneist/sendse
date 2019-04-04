
import com.o2.Communication._
import io.circe.Json
import io.circe.syntax._
import org.scalatest.{Matchers, WordSpec}
import io.circe.parser.decode

import scala.language.{higherKinds, implicitConversions, postfixOps}

class ElementsCompilerTest extends WordSpec with Matchers {
  "The PureCompiler" should {

    "translate Map" in {
       val k = submitValue(1,"d")
      val kk = decode[Command[_]](k.asJson.toString())



    }
  }
}