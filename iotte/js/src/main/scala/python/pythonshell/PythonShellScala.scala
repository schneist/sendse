package python.pythonshell

import monix.eval.Task
import monix.execution.Cancelable
import python.pythonshell.ModeEnum.Mode
import python.pythonshell.PythonShellIO.PythonShellScalaIO
import python.pythonshell.PythonShellJS.{PythonShell, PythonShellJSIO}
import shapeless.{:+:, CNil, Coproduct}

import scala.language.implicitConversions
import scala.scalajs.js
import scala.scalajs.js.{UndefOr, |}

object PythonShellIO{

  type PythonShellScalaIO = Either[String, PythonShellResult]
  type PythonShellResult =  String :+: Seq[Byte] :+: CNil

  implicit def from: String|Seq[Byte] => PythonShellScalaIO =
    p => {
      (p:Any)  match {
        case s:String => Right(Coproduct[PythonShellResult](s))
        case b:Seq[Byte] => Right(Coproduct[PythonShellResult](b))
        case pp:Any => Right(Coproduct(pp.toString))
      }
    }
}

class PythonShellScala(mode: Mode) {



  def runString(script:String, options:PythonShellOptions, callback:(Either[String, PythonShellJSIO]) => Unit) :PythonShell = {
    def transform :(js.UndefOr[String], js.UndefOr[PythonShellJSIO] ) => Either[String,PythonShellJSIO] = (err, result) => {
      if(err.isDefined  && err != null) {
        Left.apply[String,PythonShellJSIO](err.toString)
      }
      else {
        Right.apply[String,PythonShellJSIO](result.get)
      }
    }
    PythonShell.runString(script,options.toPY,(x:js.UndefOr[String],y:js.UndefOr[PythonShellJSIO]) => callback.apply(transform(x,y)))
  }

  def runTask(script:String,pythonShellOptions:PythonShellOptions):Task[PythonShellScalaIO] = {
    Task.create { (_, callback) =>
      runString(
        script,
        pythonShellOptions,
        _ match {
          case left: Left[String,PythonShellJSIO] => callback.onError(new Exception(left.value))
          case right: Right[String,PythonShellJSIO] => callback.onSuccess(PythonShellIO.from(right.value))
        }
      )
      Cancelable.empty
    }
  }


}

object EventEnum  extends Enumeration {
  type  Event = Value
  val message,stderr,close,error = Value
}

object ModeEnum  extends Enumeration {
  type Mode = Value
  val text,json,binary = Value
}

case class PythonShellOptions(
                               modeO: Option[ Mode],
                               pythonPathO :Option[String],
                               scriptPathO : Option[String],
                               pythonOptionsO : Option[Seq[String]],
                               encodingO : Option[String],
                               argsO : Option[Seq[String]]
                             ){

  private[pythonshell] implicit def toPY = new PythonShellJS.PythonShellOptions {

    override val mode: UndefOr[String] = modeO.isDefined match {
      case false => js.undefined
      case true => UndefOr.any2undefOrA(modeO.get.toString)
    }

    override val args: UndefOr[Seq[String]] = argsO.isDefined match {
      case false => js.undefined
      case true => UndefOr.any2undefOrA(argsO.get)
    }

    override val encoding: UndefOr[String] = encodingO.isDefined match {
      case false => js.undefined
      case true => UndefOr.any2undefOrA(encodingO.get)
    }

    override val pythonOptions: UndefOr[Seq[String]] = pythonOptionsO.isDefined match {
      case false => js.undefined
      case true => UndefOr.any2undefOrA(pythonOptionsO.get)
    }


    override val pythonPath: UndefOr[String] = pythonPathO.isDefined match {
      case false => js.undefined
      case true => UndefOr.any2undefOrA(modeO.get.toString)
    }

    override val scriptPath: UndefOr[String] = scriptPathO.isDefined match {
      case false => js.undefined
      case true => UndefOr.any2undefOrA(scriptPathO.get)
    }


  }
}



object PythonShellOptions{


  def empty:PythonShellOptions = new PythonShellOptions (
    modeO = None,
    pythonPathO = None,
    scriptPathO = None,
    pythonOptionsO = None,
    encodingO = None,
    argsO = None
  )

}