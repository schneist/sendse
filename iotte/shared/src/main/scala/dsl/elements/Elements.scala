package dsl.elements

import scala.concurrent.duration.FiniteDuration
import scala.language.{higherKinds, implicitConversions}


trait Operation[F[_]] {

  def Pure[Out](input: Out): F[Out]

}

trait FunctionOperation[F[_]] extends Operation[F]{

  def Map[In, Out,Context](
                            input: In,
                            function : In => Out,
                            ctx: Option[Context] = None,
                            timeout: Option[FiniteDuration] = None
                          ): F[Out]

}



case class  OperationDefinition[In, Out, Definon](
                                                  definition:Definon,
                                                  fallbackValue: Option[Out] = None,
                                                  fallbackOperation: Option[OperationDefinition[In, Out,Definon]] = None,
                                                  timeout: Option[FiniteDuration] = None,
                                                  sideEffective : Boolean = false
                                                )

