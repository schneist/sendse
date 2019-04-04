package runners

import dsl.elements.Marshalling
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration.FiniteDuration

trait MarshallingRunner[Encoding,Context,Definon]  {

  final def run[In, Out](
                          in: In,
                          definition: Definon,
                          ctx: Option[Context] = None,
                          timeout: Option[FiniteDuration] = None
                        )
                        (implicit
                         pi: Marshalling[In, Encoding],
                         po: Marshalling[Out, Encoding],
                         scheduler: Scheduler
                        ) : Task[Out] = {
    val task = pi.pack(in)
      .flatMap(p => runInternal(p, ctx, definition))
      .flatMap(p => po.unpack(p))
    timeout match {
      case Some(t) => task.timeout(t)
      case _ => task
    }
  }

   def runInternal(
                   inR:Encoding,
                   ctx: Option[Context],
                   definition: Definon
                 ): Task[Encoding]

}
