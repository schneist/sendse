package org.novogarchinsk.functions.google.cloud.firestore

import com.sun.source.doctree.SerialFieldTree
import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.{MaxSize, MinSize}
import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFirestore.{DocumentData, DocumentSnapshot}
import org.novogarchinsk.functions.{TimeSeriesStore, _}
import scalaz.zio.{IO, ZIO}
import shapeless.{::, tag, _}
import shapeless.labelled.{FieldBuilder, FieldType, KeyTag}
import record._
import ops.record._
import org.novogarchinsk.functions.google.cloud.firestore.gcloud.Extractor.SimpleExtractor
import shapeless.Witness.Aux
import syntax.singleton._

import scala.language.higherKinds
import scala.language.existentials
import scala.reflect.ClassTag
import scala.scalajs.js

object gcloud {

  //ToDo:: how to limit the id
  type StringID = String Refined MinSize[Witness.`5`.T] And MaxSize[Witness.`15`.T]

  class GCFireStore[StoredDocumentT <: Product :StoreableDocument](
                                                                    conv: (DocumentSnapshot => IO[Throwable,StoredDocumentT])
                                                                  ) extends TimeSeriesStore[StoredDocumentT,StringID, Throwable, FirebaseAdmin.FireStoreDB, ZIO] {

    type Env = FirebaseAdmin.FireStoreDB

    implicit val identifierName: IdentifierName[StoredDocumentT,StringID] = new IdentifierName[StoredDocumentT,StringID](???)

    override def getById(identifier: Identifier[StoredDocumentT,StringID]): ZIO[Env, Throwable, StoredDocumentT] = for {
      db <- ZIO.environment[Env]
      a <- ZIO.fromFuture(implicit ex =>
        db.doc(identifier.value.toString).get().toFuture)
      b <-  conv(a)
    } yield b

    override def store(value: StoredDocumentT) = ???
  }

  implicit val storedDocument : StoreableDocument[TimeValue] = new StoreableDocument[TimeValue] {
    override def identifier[With](d: TimeValue)(implicit c: TimeValue => With) =  new Identifier[TimeValue,With](c(d)) {}

    override def fieldNames() = fieldNames[TimeValue]
  }

  implicit class UnionFold[A, B](union: js.|[A, B]) {
    def fold[X](fa: A => X, fb: B => X)(implicit classTagA: ClassTag[A], classTagB: ClassTag[B]): X = (union: Any) match {
      case a: A => fa(a)
      case b: B => fb(b)
    }
  }

  trait Extractor[V]{

    def from[K](doc:DocumentData)(implicit ev: Witness.Aux[K]) : V with labelled.KeyTag[Symbol with tag.Tagged[K], V]

  }

  implicit object Extractor{

    class SimpleExtractor[V] extends Extractor[V]{

      def singletonValueName[K <: Symbol](implicit ev: Witness.Aux[K]): String = ev.value.name

      override def from[K](doc: DocumentData)(implicit ev: Witness.Aux[K]) : V with labelled.KeyTag[Symbol with tag.Tagged[K], V]  = {
        val name :String =  ??? // singletonValueName[K]

        val v :V = doc.get(name).asInstanceOf[V]

        val ret = new FieldBuilder[Symbol with tag.Tagged[K]].apply(v)
        ret
      }

    }

    // "Summoner" method
    def apply[T](implicit pack: Extractor[T]): Extractor[T] = pack

    // "Constructor" method
    def instance[T]: Extractor[T] = new SimpleExtractor[T]


  }

  abstract class ReprFact[R ]{
    def getRepr(documentSnapshot: DocumentData)(implicit ev: Witness.Aux[_]) :R
  }

  object ReprFact {

    implicit def NilRepr: ReprFact[HNil] = new ReprFact[HNil] {
      override def getRepr(documentSnapshot: DocumentData)(implicit ev: Aux[_]) = HNil
    }


    implicit def SingleRepr[K <:Symbol,V <: KeyTag[Symbol with tag.Tagged[K], V] ](implicit extractor: Extractor[V],ev: Witness.Aux[K]):ReprFact[V] =
      new ReprFact[V] {
        override def getRepr(documentSnapshot: DocumentData)(implicit ev: Witness.Aux[_]) =
          extractor.from(documentSnapshot)(ev.asInstanceOf[Witness.Aux[K]])
      }

    implicit def PairRepr[Head , Tail <: HList ](
                                                  implicit reprFactH: ReprFact[Head],
                                                  reprFactT: ReprFact[Tail],
                                                ): ReprFact[Head::Tail] = ???

  }



  object GCTimeSeriesStore extends GCFireStore[TimeValue](conv =  (a:DocumentSnapshot) => {
    a.data().fold(
      aa => ZIO.apply {
        val gen = LabelledGeneric[TimeValue]

        //val fieldN: List[String] = storedDocument.fieldNames()


        //ToDo:: add Extractor for HNil
        //ToDo:: add Extractor for  Decoder[FieldType[K, V] :+: R]


        type ds =
          Long with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"time"`.T], Long]::
            Double with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"value"`.T], Double]::
            HNil
        import ReprFact._
        implicitly[ReprFact[HNil]]


        implicit val v = new ReprFact[Long with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"time"`.T], Long]] {
          override def getRepr(documentSnapshot: DocumentData)(implicit ev: Aux[_]) = ???
        }

        implicit val vv = new ReprFact[Double with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"value"`.T], Double]] {
          override def getRepr(documentSnapshot: DocumentData)(implicit ev: Aux[_]) = ???
        }
        implicitly[ReprFact[Double with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"value"`.T], Double]]]


        val r = implicitly[ReprFact[ds]]
        // implicitly[ReprFact[Double with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"value"`.T], Double]]]
        // val tt :ds = tbd.get[ds]
        //val tt = fieldN
        //  .map(fn =>(Symbol(fn), aa.get(fn)))
        // .foldRight[HList](HNil())((a1,a2) => a1._1 ->> a1._2 :: a2)

        val tt :ds =  ??? //r.getRepr(aa)
        gen.from(tt)
        // new TimeValue(aa.get())
      }
      ,
      _ => ZIO.fail(new Exception())
    )


  })

}
















