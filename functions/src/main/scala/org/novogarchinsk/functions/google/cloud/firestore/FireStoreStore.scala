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
                                    (implicit ev: Witness.Aux[Key]):Option[FieldType[Key,Value]] = {

        Try{
          val fb = new FieldBuilder[Key]
          val v :js.Any  = doc.get(ev.value.name).get
          println("----------------------------------" +v)
          fb.apply(v.asInstanceOf[Value])
        }.toOption
      }

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

  /*
    object ReprFact {

      implicit def symbolWitness[TT](implicit tv : Witness.Aux[TT]) = new Witness {

        override type T = @@[Symbol,TT]

        override val value =  tag.apply[scala.Symbol].apply(tv.value).asInstanceOf[@@[Symbol,TT]]
      }




      implicit def SingleRepr[K, T<: @@[Symbol,K],V](
                                                      implicit extractor: FieldTypeExtractor[V]
                                                    ) = new ReprFact[V] {
        override def getRepr(documentSnapshot: DocumentData) =
          extractor.from(documentSnapshot)(symbolWitness[])
      }

      implicit def PairRepr[Head , Tail <: HList ](
                                                    implicit reprFactH: ReprFact[Head],
                                                    reprFactT: ReprFact[Tail],
                                                  ): ReprFact[Head::Tail] = ???

      def get[R](documenData: DocumentData)(implicit rf: ReprFact[R]) :R = rf.getRepr(documenData)
    }

  */

  object GCTimeSeriesStore extends GCFireStore[TimeValue](conv =  (a:DocumentSnapshot) => {
    a.data().fold(
      aa => ZIO.apply {


        /*

        type time = Witness.`"time"`.T
        type value = Witness.`"value"`.T

        type timeS = @@[Symbol,time]
        type valueS = Symbol with shapeless.tag.Tagged[value]

        type timeFT = FieldType[timeS, Long]
        type valueFT = FieldType[valueS, Double]


        type ds = timeFT :: valueFT :: HNil





        implicit val repT : ReprFact[timeFT] = ReprFact.SingleRepr[timeS ,timeFT]

        val a1 :timeFT = repT.getRepr(aa)


        implicit val repT2 : ReprFact[valueFT] = ReprFact.SingleRepr[valueS ,valueFT]

        val a2 :valueFT = repT2.getRepr(aa)


        implicit val  fdf = ReprFact.PairRepr[valueFT,HNil]
        implicit val sds = ReprFact.PairRepr[timeFT,valueFT::HNil]
        ReprFact.get[TimeValue](aa)
          */
        ???
      }
      ,
      _ => ZIO.fail(new Exception())
    )


  })

}


/**
  *
  *
  *
  * import shapeless._, labelled.{ FieldType, field }
  *
  * trait FromMap[L <: HList] {
  * def apply(m: Map[String, Any]): Option[L]
  * }
  * And then the instances:
  *
  * trait LowPriorityFromMap {
  * implicit def hconsFromMap1[K <: Symbol, V, T <: HList](implicit
  * witness: Witness.Aux[K],
  * typeable: Typeable[V],
  * fromMapT: Lazy[FromMap[T]]
  * ): FromMap[FieldType[K, V] :: T] = new FromMap[FieldType[K, V] :: T] {
  * def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = for {
  * v <- m.get(witness.value.name)
  * h <- typeable.cast(v)
  * t <- fromMapT.value(m)
  * } yield field[K](h) :: t
  * }
  * }
  *
  * object FromMap extends LowPriorityFromMap {
  * implicit val hnilFromMap: FromMap[HNil] = new FromMap[HNil] {
  * def apply(m: Map[String, Any]): Option[HNil] = Some(HNil)
  * }
  *
  * implicit def hconsFromMap0[K <: Symbol, V, R <: HList, T <: HList](implicit
  * witness: Witness.Aux[K],
  * gen: LabelledGeneric.Aux[V, R],
  * fromMapH: FromMap[R],
  * fromMapT: FromMap[T]
  * ): FromMap[FieldType[K, V] :: T] = new FromMap[FieldType[K, V] :: T] {
  * def apply(m: Map[String, Any]): Option[FieldType[K, V] :: T] = for {
  * v <- m.get(witness.value.name)
  * r <- Typeable[Map[String, Any]].cast(v)
  * h <- fromMapH(r)
  * t <- fromMapT(m)
  * } yield field[K](gen.from(h)) :: t
  * }
  * }
  * And then a helper class for convenience:
  *
  * class ConvertHelper[A] {
  * def from[R <: HList](m: Map[String, Any])(implicit
  * gen: LabelledGeneric.Aux[A, R],
  * fromMap: FromMap[R]
  * ): Option[A] = fromMap(m).map(gen.from(_))
  * }
  *
  * def to[A]: ConvertHelper[A] = new ConvertHelper[A]
  * And the example:
  *
  * case class Address(street: String, zip: Int)
  * case class Person(name: String, address: Address)
  *
  * val mp = Map(
  * "name" -> "Tom",
  * "address" -> Map("street" -> "Jefferson st", "zip" -> 10000)
  * )
  * And finally:
  *
  * scala> to[Person].from(mp)
  * res0: Option[Person] = Some(Person(Tom,Address(Jefferson st,10000)))
  *
  *
  *
  */









