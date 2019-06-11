package org.novogarchinsk.functions.google.cloud.firestore

import org.novogarchinsk.functions.google.cloud.firestore.FirebaseFunctions.FunctionsConfigFirebase
import FirebaseFirestore.{CollectionReference, DocumentReference}

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

  def firestore(): FireStoreDB = js.native


  @js.native
  class FireStoreDB extends js.Object {
    def collection(name:String) : CollectionReference = js.native
    def doc(name:String): DocumentReference = js.native
  }





  @js.native
  class FSSetwOpt extends js.Object {

  }

}