package dsl.elements

import monix.eval.Task
import monix.execution.Scheduler


/**
  * Like in https://doc.akka.io/docs/akka-http/current/common/marshalling.html
  *
  *  @tparam T type in the DSL
  *
  *  @tparam R type of the representation in the Runner
  */
final class Marshalling[T,R](val forward : T => R,
                             val backward : R => T)
                            (implicit scheduler:Scheduler){

  def pack(t:T): Task[R] = pack(Task.pure(t))

  def unpack(r:R) : Task[T] = unpack(Task.pure(r))

  def pack(t:Task[T]): Task[R] = t map forward

  def unpack(r:Task[R]) : Task[T] = r map backward

}


object Marshalling{


  // "Summoner" method
  def apply[T,R](implicit pack: Marshalling[T,R]): Marshalling[T,R] = pack

  // "Constructor" method
  def instance[T ,R](
                      forwardFunc: T => R,
                      backwardFunc: R => T)
                    (implicit scheduler:Scheduler): Marshalling[T,R] =
    new Marshalling[T,R](forwardFunc, backwardFunc)(scheduler)

  implicit def identityMarshalling[T](implicit scheduler:Scheduler): Marshalling[T,T] = new Marshalling[T,T](identity,identity)

  implicit def chainedMarshalling[A,B,C]( implicit abMarshalling: Marshalling[A,B], bcMarshalling: Marshalling[B,C],ec: Scheduler): Marshalling[A,C] =
   new Marshalling[A,C](abMarshalling.forward.andThen(bcMarshalling.forward),
    bcMarshalling.backward.andThen(abMarshalling.backward))

}

