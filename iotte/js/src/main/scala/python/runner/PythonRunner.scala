package python.runner

import monix.eval.Task
import monix.execution.Scheduler
import python.pythonshell.PythonShellIO.PythonShellScalaIO
import python.pythonshell.{ModeEnum, PythonShellOptions, PythonShellScala}
import runners.MarshallingRunner


class PythonRunner[Context]( implicit scheduler:Scheduler) extends MarshallingRunner[PythonShellScalaIO,Context ,String]{

  import  python.pythonshell.PythonShellIO._

  override def runInternal(inR: PythonShellScalaIO, ctx: Option[Context], definition: String): Task[PythonShellScalaIO] = {
    new PythonShellScala(ModeEnum.text).runTask(definition,PythonShellOptions.empty)
     }
}




