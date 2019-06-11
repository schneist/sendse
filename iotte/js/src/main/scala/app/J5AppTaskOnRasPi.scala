package demo
import dsl.elements.IoTOperation
import dsl.interpreter.monix.IoTTaskInterpreter
import johnnyfivescalajs.JohnnyFive.Board
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.language.{higherKinds, implicitConversions, postfixOps}

abstract class J5AppTaskOnRasPi extends App{

  override def main(args: Array[String]): Unit = {

    import johnnyfivescalajs.JohnnyFive._

    implicit val i = new IoTTaskInterpreter[Board]

    val board = Board(new BoardOption {override val repl = false})

    board.on("ready", () => {
      program[Task](board)
        .runToFuture
    })

  }

  def program[F[_]:cats.Monad](board:Board)(implicit o :IoTOperation.Aux[F,Board]): F[Unit]
}

