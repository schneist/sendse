package org.novogarchinsk.functions

import simulacrum.typeclass
import scala.language.higherKinds

class IdentifierName[I,N](n:N){
  def name: N = n
}


abstract class Identifier[Identified,With](val value:With){

  def name[N](implicit identifierName: IdentifierName[With,N]) : N = identifierName.name //Refined MinSize[Witness.`3`.T]  And MaxSize[Witness.`15`.T]

}

@typeclass
trait StoreableDocument[DocT]{

  def identifier[With]( d: DocT)(implicit c : DocT => With) : Identifier[DocT,With]

  def rowname:String

}


abstract class  TimeSeriesStore[V : StoreableDocument,ID ,Error <: Throwable,Environment, T[_,_,_]] {

  def getById(identifier:Identifier[V,ID]):T[Environment,Error,V]

  def store(value:V):T[Environment,Error,Unit]

}

