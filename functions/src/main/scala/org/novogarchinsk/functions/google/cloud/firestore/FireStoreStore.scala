package org.novogarchinsk.functions.google.cloud.firestore

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.{MaxSize, MinSize}
import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFirestore.{DocumentData, DocumentSnapshot}
import org.novogarchinsk.functions.{TimeSeriesStore, _}
import scalaz.zio.{IO, ZIO}
import shapeless._
import shapeless.labelled._
import shapeless.tag._
import shapeless.syntax.singleton._

import scala.language.{existentials, higherKinds, implicitConversions}
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

  trait FieldTypeExtractor[V]{


    def from[K <: Symbol ](doc:DocumentData)(implicit ev: Witness.Aux[K]) : FieldType[K, V]

  }

  object FieldTypeExtractor{

    class SimpleFieldTypeExtractor[V] extends FieldTypeExtractor[V]{

      def singletonValueName[K <: Symbol](implicit ev: Witness.Aux[K]): String = ev.value.name

      override def from[K<:Symbol](doc: DocumentData)(implicit ev: Witness.Aux[K]):FieldType[K,V] = {
        val name =  singletonValueName[K]
        val v :V = doc.get(name).asInstanceOf[V]
        new FieldBuilder[K].apply(v)
      }

    }

    // "Summoner" method
    implicit def apply[T](implicit pack: FieldTypeExtractor[T]): FieldTypeExtractor[T] = pack

    // "Constructor" method
    implicit def instance[T]: FieldTypeExtractor[T] = new SimpleFieldTypeExtractor[T]


  }

  abstract class ReprFact[R ]{
    def getRepr(documentSnapshot: DocumentData) :R
  }

  object ReprFact {

    implicit def NilRepr: ReprFact[HNil] = _ => HNil


    implicit def SingleRepr[K<: Symbol,V](
                                           implicit extractor: FieldTypeExtractor[V],
                                           ev: Witness.Aux[K]
                                         ) = new ReprFact[V] {
      override def getRepr(documentSnapshot: DocumentData) =
        extractor.from(documentSnapshot)(ev)
    }

    implicit def PairRepr[Head , Tail <: HList ](
                                                  implicit reprFactH: ReprFact[Head],
                                                  reprFactT: ReprFact[Tail],
                                                ): ReprFact[Head::Tail] = ???

  }



  object GCTimeSeriesStore extends GCFireStore[TimeValue](conv =  (a:DocumentSnapshot) => {
    a.data().fold(
      aa => ZIO.apply {


        //val fieldN: List[String] = storedDocument.fieldNames()


        //ToDo:: add Extractor for HNil
        //ToDo:: add Extractor for  Decoder[FieldType[K, V] :+: R]

        type time = Witness.`"time"`.T
        type value = Witness.`"value"`.T

        type timeS = @@[Symbol,time]
        type valueS = Symbol with shapeless.tag.Tagged[value]

        type timeFT = FieldType[timeS, Long]
        type valueFT = FieldType[valueS, Double]


        type ds = timeFT :: valueFT :: HNil

        implicit val ftet  :FieldTypeExtractor[timeFT] = FieldTypeExtractor.instance[timeFT]
        implicit val ftev  :FieldTypeExtractor[valueFT] = FieldTypeExtractor.instance[valueFT]


        implicit def v[TT](implicit tv : Witness.Aux[TT]) = new Witness {

          override type T = @@[Symbol,TT]

          override val value =  tag.apply[scala.Symbol].apply(tv.value).asInstanceOf[@@[Symbol,TT]]
        }

        implicit val repT : ReprFact[timeFT] = ReprFact.SingleRepr[timeS ,timeFT]

        val a :timeFT = repT.getRepr(aa)
        /*

        //implicitly[Extractor[Long with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"time"`.T], Long]]]

        implicit val llll = ReprFact.SingleRepr[timeS,kt]

        //implicitly[ReprFact[Long with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"time"`.T], Long]]]


        implicit val vv = new ReprFact[Double with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"value"`.T], Double]] {
          override def getRepr(documentSnapshot: DocumentData)(implicit ev: Aux[_]) = ???
        }

                val r = implicitly[ReprFact[ds]]
                // implicitly[ReprFact[Double with labelled.KeyTag[Symbol with shapeless.tag.Tagged[Witness.`"value"`.T], Double]]]
                // val tt :ds = tbd.get[ds]
                //val tt = fieldN
                //  .map(fn =>(Symbol(fn), aa.get(fn)))
                // .foldRight[HList](HNil())((a1,a2) => a1._1 ->> a1._2 :: a2)


                // new TimeValue(aa.get())
          */
        val gen = LabelledGeneric[TimeValue]
        val tt :ds =  ??? //r.getRepr(aa)
        gen.from(tt)
      }
      ,
      _ => ZIO.fail(new Exception())
    )


  })

}
















