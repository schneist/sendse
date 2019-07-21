package org.novogarchinsk.functions

import simulacrum.typeclass

import scala.language.higherKinds
import scala.language.implicitConversions
import shapeless.ops.hlist.ToTraversable
import shapeless.ops.record.Keys
import shapeless.{HList, LabelledGeneric}

class IdentifierName[I,N](n:N){
  def name: N = n
}

 class Identifier[Identified,With](val value:With){

  def name[N](implicit identifierName: IdentifierName[With,N]) : N = identifierName.name //Refined MinSize[Witness.`3`.T]  And MaxSize[Witness.`15`.T]

}

@typeclass
trait StoreableDocument[DocT <: Product]{

  def identifier[With]( d: DocT)(implicit c : DocT => With) : Identifier[DocT,With]

  trait FieldNames[T] {
    def apply(): List[(String)]
  }

  def fieldNames[T](implicit h: FieldNames[T]) = h()

  implicit def toNames[T, Repr <: HList, KeysRepr <: HList](
                                                             implicit gen: LabelledGeneric.Aux[T, Repr],
                                                             keys: Keys.Aux[Repr, KeysRepr],
                                                             traversable: ToTraversable.Aux[KeysRepr, List, Symbol]
                                                           ): FieldNames[T] = {
    () => keys().toList.map(a => (a.name))
  }
  def fieldNames() : List[String]
}


abstract class  TimeSeriesStore[V <:Product : StoreableDocument,ID ,Error <: Throwable,Environment, T[_,_,_]] {



  def getById(identifier:Identifier[V,ID]):T[Environment,Error,V]

 // def store(value:V):T[Environment,Error,Unit]


}
