package demo

import definitions.PythonDefinitions
import dsl.elements.MarshalledOperation
import dsl.interpreter.monix.MarshalledTaskInterpreter
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import python.pythonshell.{ModeEnum, PythonShellScala}
import python.runner.PythonRunner

import scala.concurrent.duration._
import scala.language.{higherKinds, implicitConversions, postfixOps}


object PythonApp{

  val pr = new PythonRunner[Unit]()



  implicit val mo = new MarshalledTaskInterpreter[String]


  def program[F[_]]
  (implicit o :MarshalledOperation[F]): F[String ] = {
    import PythonDefinitions._
    import o._
    Run("",runScript(py_getValues_script),Some(new PythonShellScala(ModeEnum.text)))
  }

  def main(args: Array[String]): Unit = {
    program(mo)
      .onErrorHandleWith((t:Throwable) => Task.now(t.toString + t.getStackTrace.mkString(System.lineSeparator())))
      .timeout(10 second)
      .runAsync(t =>  println(t))

  }
}
