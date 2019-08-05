package org.novogarchinsk.functions.google.cloud.firestore

import eu.timepit.refined.types.string.NonEmptyString
import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFirestore.{DocumentData, DocumentSnapshot}
import org.novogarchinsk.functions.{TimeSeriesStore, _}
import zio.{IO, ZIO}
import shapeless.Witness.Aux
import shapeless._
import shapeless.labelled.{FieldType, _}

import scala.language.{existentials, higherKinds, implicitConversions}
import scala.reflect.ClassTag
import scala.scalajs.js
import scala.util.Try

object gcloud {

  type StringID = NonEmptyString

  class GCFireStore[StoredDocumentT <: Product :StoreableDocument](
                                                                    conv: (DocumentSnapshot => IO[Throwable,StoredDocumentT])
                                                                  ) extends TimeSeriesStore[StoredDocumentT,StringID, Throwable,  FirebaseFirestore.Firestore, ZIO] {

    type Env =  FirebaseFirestore.Firestore


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

    def from[K <: Symbol ](doc:DocumentData)(implicit ev: Witness.Aux[K],
                                             typeable: Typeable[V]) : Option[FieldType[K, V]]

  }

  object FieldTypeExtractor{

    class SimpleFieldTypeExtractor[Value] extends FieldTypeExtractor[Value] {



      override def from[Key <: Symbol](doc: DocumentData)
                                      (implicit ev: Witness.Aux[Key],
                                       typeable: Typeable[Value]
                                      ): Option[FieldType[Key, Value]] = {
         doc.get(ev.value.name)
        .flatMap(v => typeable.cast(v))
          .map(v => new FieldBuilder[Key].apply(v))
      }

    }


    class ProductFieldTypeExtractor[Value <:Product,R <:HList](
                                                                implicit gen: shapeless.LabelledGeneric.Aux[Value,R],
                                                                reprFact: ReprFact[R]) extends FieldTypeExtractor[Value]{

      override def from[K <: Symbol](doc: DocumentData)
                                    (implicit ev: Aux[K],
                                     typeable: Typeable[Value]) = {
        Try{
          val r = doc.get(ev.value.name).map(_.asInstanceOf[DocumentData]).get
          val t = to[Value]

          t.from[R](r)(gen,reprFact).get
        }.toOption
          .map(v => new FieldBuilder[K].apply(v))
      }
    }

    implicit def instance[T <: AnyVal ]: FieldTypeExtractor[T] = new SimpleFieldTypeExtractor[T]

    implicit def instanceProd[T <: Product,R  <: HList](
                                                implicit gen: shapeless.LabelledGeneric.Aux[T,R],
                                                reprFact: ReprFact[R]): FieldTypeExtractor[T] = new ProductFieldTypeExtractor[T,R]

    implicit def instanceString: FieldTypeExtractor[String] = new SimpleFieldTypeExtractor[String]

    // implicit def instanceALL[T]: FieldTypeExtractor[T] = new SimpleFieldTypeExtractor[T]



  }


  trait ReprFact[R <: HList]{

    def apply(documentSnapshot: DocumentData) : Option[R]

  }

  trait LowPriorityReprFact {

    implicit def hconsFromReprFact1[K <: Symbol, V, T <: HList](implicit
                                                                witnessK: Witness.Aux[K],
                                                                extractor: FieldTypeExtractor[V],
                                                                factRem: Lazy[ReprFact[T]],
                                                                typeable: Typeable[V]
                                                               ): ReprFact[FieldType[K, V] :: T] = (documentSnapshot: DocumentData) => for {
      v <- extractor.from(documentSnapshot)
      t <- factRem.value(documentSnapshot)
    } yield v :: t

  }

  object ReprFact extends LowPriorityReprFact{

    implicit def NilRepr: ReprFact[HNil] = _ => Some(HNil)

    implicit def hconsFromReprFact0[K <: Symbol, V, R <: HList, T <: HList](implicit
                                                                            witnessK: Witness.Aux[K],
                                                                            extractor: FieldTypeExtractor[V],
                                                                            gen: LabelledGeneric.Aux[V, R],
                                                                            fromMapH: ReprFact[R],
                                                                            fromMapT: ReprFact[T],
                                                                            typeable: Typeable[V]
                                                                           ): ReprFact[FieldType[K, V] :: T] = (documentSnapshot: DocumentData) => for {
      v <- extractor.from(documentSnapshot)
      t <- fromMapT(documentSnapshot)
    } yield v :: t

  }

  class ConvertHelper[A] {
    def from[R <: HList](m: DocumentData)(implicit
                                          gen: LabelledGeneric.Aux[A, R],
                                          reprFact: ReprFact[R]
    ): Option[A] = reprFact(m).map(gen.from(_))
  }

  def to[A]: ConvertHelper[A] = new ConvertHelper[A]

  object GCTimeSeriesStore extends GCFireStore[TimeValue](conv =  (a:DocumentSnapshot) => {
    a.data().fold(
      aa => ZIO.apply{
        val t :ConvertHelper[TimeValue] = to[TimeValue]
        t.from(aa).get
      }
      ,
      _ => ZIO.fail(new Exception("No data in Snapshot"))
    )
  })

}
