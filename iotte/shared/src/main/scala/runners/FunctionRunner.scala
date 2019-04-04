package runners

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration.FiniteDuration

object FunctionRunner {

  def run[In, Out](in: In,
                   function: In => Out,
                   timeout: Option[FiniteDuration] = None)
                  (implicit scheduler: Scheduler): Task[Out] = {
    val task = Task.apply( function(in))
    timeout match {
      case Some(t) => task.timeout(t)
      case _ => task
    }
  }
}
