package org.novogarchinsk.functions.jsimport

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal
import scala.scalajs.js.{Date, Promise, |}

@js.native
@JSGlobal
object FirebaseFirestore extends scalajs.js.Object {

  trait Settings extends js.Object {
    var projectId: js.UndefOr[String] = js.undefined
    var keyFilename: js.UndefOr[String] = js.undefined
    var credentials: js.UndefOr[Credentials] = js.undefined
    var timestampsInSnapshots: js.UndefOr[Boolean] = js.undefined
  }

  trait Credentials extends js.Object {
    var client_email: js.UndefOr[String] = js.undefined
    var private_key: js.UndefOr[String] = js.undefined
  }

  @js.native
  class Firestore protected () extends js.Object {
    def this(settings: js.UndefOr[Settings ]) = this()
    def settings(settings: Settings): Unit = js.native
    def collection(collectionPath: String): CollectionReference = js.native
    def doc(documentPath: String): DocumentReference = js.native
    def getAll(documentRefsOrReadOptions: DocumentReference | ReadOptions*): Promise[js.Array[DocumentSnapshot]] = js.native
    def getCollections(): Promise[js.Array[CollectionReference]] = js.native
    def listCollections(): Promise[js.Array[CollectionReference]] = js.native
    def runTransaction[T](updateFunction: js.Function1[Transaction, Promise[T]], transactionOptions: js.UndefOr[js.Any ]): Promise[T] = js.native
    def batch(): WriteBatch = js.native
  }

  @js.native
  class GeoPoint protected () extends js.Object {
    def this(latitude: Double, longitude: Double) = this()
    def latitude: Double = js.native
    def longitude: Double = js.native
    def isEqual(other: GeoPoint): Boolean = js.native
  }

  @js.native
  class Transaction extends js.Object {
    def get(query: Query): Promise[QuerySnapshot] = js.native
    def get(documentRef: DocumentReference): Promise[DocumentSnapshot] = js.native
    def getAll(documentRefsOrReadOptions: DocumentReference | ReadOptions*): Promise[js.Array[DocumentSnapshot]] = js.native
    def create(documentRef: DocumentReference, data: DocumentData): Transaction = js.native
    def set(documentRef: DocumentReference, data: DocumentData, options: js.UndefOr[SetOptions ]): Transaction = js.native
    def update(documentRef: DocumentReference, data: UpdateData, precondition: js.UndefOr[Precondition ]): Transaction = js.native
    def update(documentRef: DocumentReference, field: String | FieldPath, value: js.Any, fieldsOrPrecondition: js.Any*): Transaction = js.native
    def delete(documentRef: DocumentReference, precondition:js.UndefOr[Precondition ]): Transaction = js.native
  }

  @js.native
  class WriteBatch extends js.Object {
    def create(documentRef: DocumentReference, data: DocumentData): WriteBatch = js.native
    def set(documentRef: DocumentReference, data: DocumentData, options: js.UndefOr[SetOptions]): WriteBatch = js.native
    def update(documentRef: DocumentReference, data: UpdateData, precondition: js.UndefOr[Precondition]): WriteBatch = js.native
    def update(documentRef: DocumentReference, field: String | FieldPath, value: js.Any, fieldsOrPrecondition: js.Any*): WriteBatch = js.native
    def delete(documentRef: DocumentReference, precondition: js.UndefOr[Precondition]): WriteBatch = js.native
    def commit(): Promise[js.Array[WriteResult]] = js.native
  }

  trait Precondition extends js.Object {
    def lastUpdateTime: js.UndefOr[Timestamp] = js.undefined
  }

  trait SetOptions extends js.Object {
    val merge: js.UndefOr[Boolean] = js.undefined
    val mergeFields: js.UndefOr[js.Array[String | FieldPath]] = js.undefined
  }

  trait ReadOptions extends js.Object {
    def fieldMask: js.UndefOr[js.Array[String | FieldPath]] = js.undefined
  }

  @js.native
  class WriteResult extends js.Object {
    def writeTime: Timestamp = js.native
    def isEqual(other: WriteResult): Boolean = js.native
  }

  @js.native
  class DocumentReference extends js.Object {
    def id: String = js.native
    def firestore: Firestore = js.native
    def parent: CollectionReference = js.native
    def path: String = js.native
    def collection(collectionPath: String): CollectionReference = js.native
    def getCollections(): Promise[js.Array[CollectionReference]] = js.native
    def listCollections(): Promise[js.Array[CollectionReference]] = js.native
    def create(data: DocumentData): Promise[WriteResult] = js.native
    def set(data: DocumentData, options: js.UndefOr[SetOptions ]): Promise[WriteResult] = js.native
    def update(data: UpdateData, precondition: js.UndefOr[Precondition ]): Promise[WriteResult] = js.native
    def update(field: String | FieldPath, value: js.Any, moreFieldsOrPrecondition: js.Any*): Promise[WriteResult] = js.native
    def delete(precondition: js.UndefOr[Precondition ]): Promise[WriteResult] = js.native
    def get(): Promise[DocumentSnapshot] = js.native
    def onSnapshot(onNext: js.Function1[DocumentSnapshot, Unit], onError: js.UndefOr[js.Function1[Error, Unit] ]): js.Function0[Unit] = js.native
    def isEqual(other: DocumentReference): Boolean = js.native
  }

  @js.native
  class DocumentSnapshot extends js.Object {
    def exists: Boolean = js.native
    def ref: DocumentReference = js.native
    def id: String = js.native
    def createTime: Timestamp = js.native
    def updateTime: Timestamp = js.native
    def readTime: Timestamp = js.native
    def data(): DocumentData | Unit = js.native
    def get(fieldPath: String | FieldPath): js.Dynamic = js.native
    def isEqual(other: DocumentSnapshot): Boolean = js.native
  }

  @js.native
  class QueryDocumentSnapshot extends DocumentSnapshot {
    override def createTime: Timestamp = js.native
    override def updateTime: Timestamp = js.native
    override def data(): DocumentData | Unit= js.native
  }

  @js.native
  class Query extends js.Object {
    def firestore: Firestore = js.native
    def where(fieldPath: String | FieldPath, opStr: WhereFilterOp, value: js.Any): Query = js.native
    def orderBy(fieldPath: String | FieldPath, directionStr: js.UndefOr[OrderByDirection ]): Query = js.native
    def limit(limit: Double): Query = js.native
    def offset(offset: Double): Query = js.native
    def select(field: String | FieldPath*): Query = js.native
    def startAt(snapshot: DocumentSnapshot): Query = js.native
    def startAt(fieldValues: js.Any*): Query = js.native
    def startAfter(snapshot: DocumentSnapshot): Query = js.native
    def startAfter(fieldValues: js.Any*): Query = js.native
    def endBefore(snapshot: DocumentSnapshot): Query = js.native
    def endBefore(fieldValues: js.Any*): Query = js.native
    def endAt(snapshot: DocumentSnapshot): Query = js.native
    def endAt(fieldValues: js.Any*): Query = js.native
    def get(): Promise[QuerySnapshot] = js.native
    //def stream(): ReadableStream[_] = js.native
    def onSnapshot(onNext: js.Function1[QuerySnapshot, Unit], onError: js.UndefOr[js.Function1[Error, Unit]]): js.Function0[Unit] = js.native
    def isEqual(other: Query): Boolean = js.native
  }

  @js.native
  class QuerySnapshot extends js.Object {
    def query: Query = js.native
    def docs: js.Array[QueryDocumentSnapshot] = js.native
    def size: Double = js.native
    def empty: Boolean = js.native
    def readTime: Timestamp = js.native
    def docChanges(): js.Array[DocumentChange] = js.native
    def forEach(callback: js.Function1[QueryDocumentSnapshot, Unit], thisArg: js.UndefOr[js.Any] ): Unit = js.native
    def isEqual(other: QuerySnapshot): Boolean = js.native
  }

  trait DocumentChange extends js.Object {
    def `type`: js.UndefOr[DocumentChangeType] = js.undefined
    def doc: js.UndefOr[QueryDocumentSnapshot] = js.undefined
    def oldIndex: js.UndefOr[Double] = js.undefined
    def newIndex: js.UndefOr[Double] = js.undefined
  }

  @js.native
  class CollectionReference extends Query {
    def id: String = js.native
    def parent: DocumentReference | Null = js.native
    def path: String = js.native
    def listDocuments(): Promise[js.Array[DocumentReference]] = js.native
    def doc(documentPath: js.UndefOr[String]): DocumentReference = js.native
    def add(data: DocumentData): Promise[DocumentReference] = js.native
    def isEqual(other: CollectionReference): Boolean = js.native
  }

  @js.native
  class FieldValue extends js.Object {
    def isEqual(other: FieldValue): Boolean = js.native
  }

  @js.native
  object FieldValue extends js.Object {
    def serverTimestamp(): FieldValue = js.native
    def delete(): FieldValue = js.native
    def arrayUnion(elements: js.Any*): FieldValue = js.native
    def arrayRemove(elements: js.Any*): FieldValue = js.native
  }

  @js.native
  class FieldPath protected () extends js.Object {
    def this(fieldNames: String*) = this()
    def isEqual(other: FieldPath): Boolean = js.native
  }

  @js.native
  object FieldPath extends js.Object {
    def documentId(): FieldPath = js.native
  }

  @js.native
  class Timestamp protected () extends js.Object {
    def this(seconds: Double, nanoseconds: Double) = this()
    def seconds: Double = js.native
    def nanoseconds: Double = js.native
    def toDate(): Date = js.native
    def toMillis(): Double = js.native
    def isEqual(other: Timestamp): Boolean = js.native
  }

  @js.native
  object Timestamp extends js.Object {
    def now(): Timestamp = js.native
    def fromDate(date: Date): Timestamp = js.native
    def fromMillis(milliseconds: Double): Timestamp = js.native
  }


  type DocumentData = js.Dictionary[js.Any]
  type UpdateData = js.Dictionary[js.Any]
  def setLogFunction(logger: js.Function1[String, Unit]): Unit = js.native
  type OrderByDirection = String
  type WhereFilterOp = String
  type DocumentChangeType = String
}
