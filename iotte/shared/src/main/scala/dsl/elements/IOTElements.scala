package dsl.elements

import org.reactivestreams.{Publisher, Subscriber}

import scala.language.{higherKinds, implicitConversions}


trait IoTOperation[F[_]]  {

  type Context

  def doOnce[In, Out](
                       input: In,
                       operationDefinition: OperationDefinition[In, Out,(In,Context)=> Out],
                       ctx: Context
                     ): F[Out]

  def waitFor[In, Out](
                        input: In,
                        operationDefinition: OperationDefinition[In, Out,(In,Context,Subscriber[Out])=> Unit],
                        ctx: Context
                      ): F[Out]

  def source[In,Out]( input: In,
                      operationDefinition: OperationDefinition[In, Out,(In,Context)=> Publisher[Out]],
                      ctx: Context
                 ): F[Publisher[Out]]

  def sink[In,Out]( input: In,
                      operationDefinition: OperationDefinition[In, Out,(In,Context)=> Subscriber[Out]],
                    ctx: Context
                    ): F[Subscriber[Out]]



}

object IoTOperation {

  type Aux[T[_], Repr0] = IoTOperation[T] {type Context = Repr0}

  def apply[T[_], R](implicit pack: IoTOperation[T]): IoTOperation[T] = pack

}

