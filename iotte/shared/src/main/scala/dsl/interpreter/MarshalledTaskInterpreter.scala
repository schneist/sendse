package dsl.interpreter

import cats.implicits._
import dsl.elements._
import monix.eval.Task
import monix.execution.Scheduler
import monix.execution.Scheduler.Implicits.global
import runners.{FunctionRunner, MarshallingRunner}

import scala.concurrent.duration.FiniteDuration
import scala.language.higherKinds

class TaskInterpreter extends Operation[Task]{
  override def Pure[Out](input: Out): Task[Out] = Task.pure(input)
}


class FunctionInterpreter[RuntimeEncoding]  extends TaskInterpreter with  FunctionOperation[Task] {

  override def Map[In, Out, Context](input: In,
                                     function: In => Out,
                                     ctx: Option[Context] = None,
                                     timeout: Option[FiniteDuration] = None): Task[Out] = {
    FunctionRunner.run(input,function,timeout )
  }
}


class MarshalledTaskInterpreter[RuntimeEncoding]  extends TaskInterpreter with  MarshalledOperation[Task] {




  override def Run[In, Out, RuntimeEncoding,Context,Definon](input: In,
                                                             operationDefinition: OperationDefinition[In, Out,Definon],
                                                             ctx: Option[Context] = None,
                                                            )
                                                            (implicit
                                                             runner: MarshallingRunner[RuntimeEncoding,Context,Definon],
                                                             pi: Marshalling[In, RuntimeEncoding],
                                                             po: Marshalling[Out, RuntimeEncoding]): Task[Out] = {
    toTask( operationDefinition,input, ctx)
  }

  override def Map[In, Out, RuntimeEncoding,Context,Definon](input: Traversable[In],
                                                             operationMap: OperationDefinition[In, Out ,Definon],
                                                             ctx: Option[Context] = None,
                                                            )
                                                            (implicit
                                                             runner: MarshallingRunner[RuntimeEncoding,Context,Definon],
                                                             pi: Marshalling[In, RuntimeEncoding],
                                                             po: Marshalling[Out, RuntimeEncoding]
                                                            ): Task[Traversable[Out]] = {
    input.toList.traverse(i => Run(i, operationMap, ctx)).map(_.asInstanceOf[Traversable[Out]])
  }

  override def Filter[InOut, RuntimeEncoding,Context,Definon](input: Traversable[InOut],
                                                              operationCond: OperationDefinition[InOut, Boolean,Definon],
                                                              ctx: Option[Context] = None,
                                                             )
                                                             (implicit
                                                              runner: MarshallingRunner[RuntimeEncoding,Context,Definon],
                                                              pi: Marshalling[InOut, RuntimeEncoding],
                                                              pb: Marshalling[Boolean, RuntimeEncoding],
                                                             ): Task[Traversable[InOut]] = {
    Map(input, operationCond, ctx).map(
      _.toSeq
        .zip(input.toSeq)
        .filter(c => c._1)
        .map(d => d._2))
  }

  override def Fold[InOut, RuntimeEncoding,Context,Definion](
                                                              input: Traversable[InOut],
                                                              initial: InOut,
                                                              operationMap: OperationDefinition[(InOut, InOut), InOut, Definion],
                                                              ctx: Option[Context] = None,
                                                            )
                                                            (implicit
                                                             runner: MarshallingRunner[RuntimeEncoding,Context,Definion],
                                                             pp: Marshalling[(InOut, InOut), RuntimeEncoding],
                                                             pi: Marshalling[InOut, RuntimeEncoding],
                                                            ): Task[InOut] = {
    var acc = Task.pure(initial)
    input.foreach(i => acc = acc.flatMap(a => Run((a, i), operationMap, ctx)))
    acc
  }



  def toTask[In, Out, RuntimeEncoding,Context,Definon](
                                                        operationDefinition: OperationDefinition[In, Out,Definon],
                                                        input: In,
                                                        ctx: Option[Context] = None ,
                                                      )
                                                      (implicit
                                                       runner: MarshallingRunner[RuntimeEncoding,Context,Definon],
                                                       pi: Marshalling[In, RuntimeEncoding],
                                                       po: Marshalling[Out, RuntimeEncoding],
                                                       global: Scheduler): Task[Out] = {
    runner.run(input, operationDefinition.definition,ctx,operationDefinition.timeout)(pi, po, global)
      .transformWith(tt => Task.now(tt), (t: Throwable) => {
        operationDefinition.fallbackOperation match {
          case Some(fb) => toTask( fb,input,ctx)
          case _ => Task.raiseError(t)
        }
      })
      .transformWith(tt => Task.now(tt), (t: Throwable) => {
        operationDefinition.fallbackValue match {
          case Some(fb) => Task.now(fb)
          case _ => Task.raiseError(t)
        }
      })
  }

}
