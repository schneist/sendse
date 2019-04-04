package dsl.elements

import runners.MarshallingRunner
import scala.language.higherKinds

trait MarshalledOperation[F[_] ] extends Operation[F] {


  def Run[In, Out, RuntimeEncoding,Context,Definon](
                                                     input: In,
                                                     operationDefinition: OperationDefinition[In, Out,Definon],
                                                     ctx: Option[Context] = None
                                                   )
                                                   (implicit
                                                    runner: MarshallingRunner[RuntimeEncoding,Context,Definon],
                                                    pi: Marshalling[In, RuntimeEncoding],
                                                    po: Marshalling[Out, RuntimeEncoding]
                                                   ): F[Out]

  def Map[In, Out, RuntimeEncoding,Context,Definon](
                                                     input: Traversable[In],
                                                     operationMap: OperationDefinition[In, Out,Definon],
                                                     ctx: Option[Context] = None
                                                   )
                                                   (implicit
                                                    runner: MarshallingRunner[RuntimeEncoding,Context,Definon],
                                                    pi: Marshalling[In, RuntimeEncoding],
                                                    po: Marshalling[Out, RuntimeEncoding]
                                                   ): F[Traversable[Out]]

  def Filter[InOut, RuntimeEncoding,Context,Definon](
                                                      input: Traversable[InOut],
                                                      operationCond: OperationDefinition[InOut, Boolean,Definon],
                                                      ctx: Option[Context] = None
                                                    )
                                                    (implicit
                                                     runner: MarshallingRunner[RuntimeEncoding,Context,Definon],
                                                     pi: Marshalling[InOut, RuntimeEncoding],
                                                     pb: Marshalling[Boolean, RuntimeEncoding]
                                                    ): F[Traversable[InOut]]

  def Fold[InOut, RuntimeEncoding,Context,Definon](
                                                    input: Traversable[InOut],
                                                    initial: InOut,
                                                    operationMap: OperationDefinition[(InOut, InOut), InOut,Definon],
                                                    ctx: Option[Context] = None
                                                  )
                                                  (implicit
                                                   runner: MarshallingRunner[RuntimeEncoding,Context,Definon],
                                                   pp: Marshalling[(InOut, InOut), RuntimeEncoding],
                                                   pi: Marshalling[InOut, RuntimeEncoding],
                                                  ): F[InOut]
}
