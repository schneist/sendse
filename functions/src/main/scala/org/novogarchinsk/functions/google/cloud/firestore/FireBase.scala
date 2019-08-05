package org.novogarchinsk.functions.google.cloud.firestore

import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFunctions.FunctionsConfigFirebase
import FirebaseFirestore.{CollectionReference, DocumentReference, Firestore}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


@js.native
@JSImport("firebase-functions", JSImport.Namespace)
object FirebaseFunctions extends js.Object {

  def config():FunctionsConfig = js.native

  @js.native
  class  FunctionsConfig extends js.Object {
    def firebase: FunctionsConfigFirebase =  js.native
  }

  @js.native
  class FunctionsConfigFirebase extends js.Object {
  }
}

@js.native
@JSImport("firebase-admin", JSImport.Namespace)
object FirebaseAdmin extends js.Object {

  def initializeApp(functionsConfigFirebase: FunctionsConfigFirebase ) : Unit = js.native

  def firestore(): Firestore = js.native


}