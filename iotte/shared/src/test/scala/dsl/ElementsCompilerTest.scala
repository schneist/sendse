package dsl

import java.util.UUID

import cats.Monad
import cats.implicits._
import dsl.elements.{MarshalledOperation, OperationDefinition}
import dsl.interpreter.MarshalledTaskInterpreter
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{Matchers, WordSpec}
import runners.{MapRunner, MarshallingRunner}

import scala.collection.Map
import scala.language.{higherKinds, implicitConversions, postfixOps}

class ElementsCompilerTest extends WordSpec with Matchers {
  "The PureCompiler" should {

    "translate Map" in {
      val doubleID = UUID.randomUUID()
      val divideby0ID = UUID.randomUUID()

      implicit val runner  : MarshallingRunner[Int,String,UUID] =
        new MapRunner[Int,String](Map(doubleID -> (i => 2 * i), divideby0ID -> (_ => throw new Exception(""))))
      val multby2 =
        new OperationDefinition[Int, Int, UUID](doubleID,Some(2))
      val divideby0 =
        new OperationDefinition[Int, Int, UUID](divideby0ID,Some(2))

      def program[F[_]:Monad](implicit
                              o :MarshalledOperation[F]
                             ): F[Int] = {
        import cats.syntax.functor._
        import o._
        for {
          a <- Run(1, divideby0)
          b <- Run(a, multby2)
        }  yield b

      }


      def run() = {
        implicit val c = new MarshalledTaskInterpreter[Int]
        program[Task]
      }


      run.runToFuture.onComplete(r => r.get should be(4))




    }
  }
}