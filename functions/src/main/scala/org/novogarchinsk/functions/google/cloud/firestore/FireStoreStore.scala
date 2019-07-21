package org.novogarchinsk.functions.google.cloud.firestore

import eu.timepit.refined.api.Refined
import eu.timepit.refined.boolean.And
import eu.timepit.refined.collection.{MaxSize, MinSize}
import eu.timepit.refined.types.string.NonEmptyString
import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFirestore.{DocumentData, DocumentSnapshot}
import org.novogarchinsk.functions.{TimeSeriesStore, _}
import scalaz.zio.{IO, ZIO}
import shapeless._
import shapeless.labelled._
import shapeless.tag._
import shapeless.syntax.singleton._
import shapeless._
import labelled.{FieldType, field}

import scala.language.{existentials, higherKinds, implicitConversions}
import scala.reflect.ClassTag
import scala.scalajs.js
import scala.util.Try

object gcloud {

  type StringID = NonEmptyString

  class GCFireStore[StoredDocumentT <: Product :StoreableDocument](
                                                                    conv: (DocumentSnapshot => IO[Throwable,StoredDocumentT])
                                                                  ) extends TimeSeriesStore[StoredDocumentT,StringID, Throwable, FirebaseAdmin.FireStoreDB, ZIO] {

    type Env =   FirebaseAdmin.FireStoreDB


    override def getById(identifier: Identifier[StoredDocumentT,StringID]): ZIO[Env, Throwable, StoredDocumentT] = for {
      db <- ZIO.environment[Env]
      a <- ZIO.fromFuture(implicit ex =>
        db.doc(identifier.value.toString).get().toFuture)
      b <-  conv(a)
    } yield b

    // override def store(value: StoredDocumentT) = ???
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

    def from[K <: Symbol ](doc:DocumentData)(implicit ev: Witness.Aux[K]) : Option[FieldType[K, V]]

  }

  object FieldTypeExtractor{

    class SimpleFieldTypeExtractor[Value] extends FieldTypeExtractor[Value]{

      override def from[Key<:Symbol](doc: DocumentData)
                                    (implicit ev: Witness.Aux[Key]):Option[FieldType[Key,Value]] = Try{
        new FieldBuilder[Key].apply( doc.get(ev.value.name).get.asInstanceOf[Value])
      }.toOption

    }

    implicit def instance[T]: FieldTypeExtractor[T] = new SimpleFieldTypeExtractor[T]

  }


  trait ReprFact[R <: HList]{

    def apply(documentSnapshot: DocumentData) : Option[R]

  }

  trait LowPriorityReprFact {

    implicit def hconsFromReprFact1[K <: Symbol, V, T <: HList](implicit
                                                                witness: Witness.Aux[K],
                                                                extractor: FieldTypeExtractor[V],
                                                                factRem: Lazy[ReprFact[T]]
                                                               ): ReprFact[FieldType[K, V] :: T] = (documentSnapshot: DocumentData) => for {
      v <- extractor.from(documentSnapshot)
      t <- factRem.value(documentSnapshot)
    } yield v :: t

  }

  object ReprFact extends LowPriorityReprFact{

    implicit def NilRepr: ReprFact[HNil] = _ => Some(HNil)


    implicit def hconsFromReprFact0[K <: Symbol, V, R <: HList, T <: HList](implicit
                                                                            witness: Witness.Aux[K],
                                                                            extractor: FieldTypeExtractor[V],
                                                                            gen: LabelledGeneric.Aux[V, R],
                                                                            fromMapH: ReprFact[R],
                                                                            fromMapT: ReprFact[T]
                                                                           ): ReprFact[FieldType[K, V] :: T] = new ReprFact[FieldType[K, V] :: T] {
      def apply(documentSnapshot: DocumentData): Option[FieldType[K, V] :: T] = for {
        v <- extractor.from(documentSnapshot)
        t <- fromMapT(documentSnapshot)
      } yield v :: t

    }
  }


  class ConvertHelper[A] {
    def from[R <: HList](m: DocumentData)(implicit
                                          gen: LabelledGeneric.Aux[A, R],
                                          fromMap: ReprFact[R]
    ): Option[A] = fromMap(m).map(gen.from(_))
  }

  def to[A]: ConvertHelper[A] = new ConvertHelper[A]



  object GCTimeSeriesStore extends GCFireStore[TimeValue](conv =  (a:DocumentSnapshot) => {
    a.data().fold(
      aa => ZIO.apply {
        to[TimeValue].from(aa).get
      }
      ,
      _ => ZIO.fail(new Exception())
    )


  })

}
