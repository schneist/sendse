package runners

import java.util.UUID

import monix.eval.Task
import monix.execution.Scheduler

class MapRunner[R,S](map: scala.collection.Map[UUID, R => R])
                    (
                      implicit scheduler:Scheduler
                    ) extends MarshallingRunner[R,S,UUID] {

  override def runInternal(inR: R ,a: Option[S], identifier: UUID): Task[R] =
    Task.pure(map.getOrElse(identifier, (x: R) => x)(inR))

}
