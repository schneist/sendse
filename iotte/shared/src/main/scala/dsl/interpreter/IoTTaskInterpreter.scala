package dsl.interpreter

import dsl.elements.{IoTOperation, OperationDefinition}
import monix.eval.Task
import monix.execution.Cancelable
import org.reactivestreams.{Publisher, Subscriber, Subscription}

class IoTTaskInterpreter[C] extends  IoTOperation[Task] {

  override type Context = C

  override def doOnce[In, Out](input: In,
                               operationDefinition: OperationDefinition[In, Out, (In, C) => Out],
                               ctx: Context
                              ): Task[Out] = {
    Task.create { (_, callback) =>
      callback.onSuccess(operationDefinition.definition(input, ctx))
      Cancelable.empty
    }
  }

  override def waitFor[In, Out](input: In,
                                operationDefinition: OperationDefinition[In, Out, (In, C, Subscriber[Out]) => Unit],
                                ctx: Context
                               ): Task[Out] = {
    Task.create[Out] {
      (_, callback) => {
        val subs =  new Subscriber[Out] {
          override def onSubscribe(s: Subscription): Unit = {s.request(1)}

          override def onNext(t: Out): Unit = callback.onSuccess(t)

          override def onError(t: Throwable): Unit = callback.onError(t)

          override def onComplete(): Unit = {}
        }
        operationDefinition.definition(input, ctx, subs)
        Cancelable.empty
      }
    }
  }
  override def source[In, Out](
                                input: In,
                                operationDefinition: OperationDefinition[In, Out, (In, C) => Publisher[Out]],
                                ctx: Context
                              ): Task[Publisher[Out]] = {
    Task.pure(operationDefinition.definition(input,ctx))
  }

  override def sink[In, Out](
                              input: In,
                              operationDefinition: OperationDefinition[In, Out, (In, C) => Subscriber[Out]],
                              ctx: C
                            ): Task[Subscriber[Out]] = {
    Task.pure(operationDefinition.definition(input,ctx))
  }
}
