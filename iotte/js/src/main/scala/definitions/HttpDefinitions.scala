package definitions

import java.net.URLEncoder

import dsl.elements.OperationDefinition
import fr.hmil.roshttp.{HttpRequest, Method, Protocol}
import fr.hmil.roshttp.body.PlainTextBody
import monix.execution.Scheduler.Implicits.global
import org.reactivestreams.{Subscriber, Subscription}

object HttpDefinitions {

/*
  def SinkHttp : OperationDefinition[HttpRequest,Unit,HttpRequest=> Subscriber[String]] = new OperationDefinition[HttpRequest,Unit,HttpRequest => Subscriber[String]](
    (request: HttpRequest,_) => new Subscriber[String] {
      override def onSubscribe(s: Subscription): Unit = {}

      override def onNext(elem: String): Unit = {
        request.withBody(PlainTextBody(elem))
        request.send()
      }

      override def onError(ex: Throwable): Unit = {}

      override def onComplete(): Unit = {}
    }
  )
*/
  type lat
  type long
  type cords = (lat,long)


  val request : HttpRequest = HttpRequest("europe-west1-codekommune.cloudfunctions.net")
    .withProtocol(Protocol.HTTPS)
    .withMethod(Method.GET)
    .withPath("updateLocation")

  def SinkUpdateLoacation(id:String): OperationDefinition[Unit,Unit,Unit => Subscriber[cords]] =
    new OperationDefinition[Unit,Unit,Unit => Subscriber[cords]](

      _ => new Subscriber[cords] {

        override def onSubscribe(s: Subscription): Unit = {}

        override def onNext(elem: cords): Unit = {
          request.withQueryParameter("id",id)
          .withQueryParameter("latitude",URLEncoder.encode(elem._1.toString,"UTF-8"))
          .withQueryParameter("longitude",URLEncoder.encode(elem._2.toString,"UTF-8"))
          .send()
        }

        override def onError(ex: Throwable): Unit = {}

        override def onComplete(): Unit = {}
      }
    )

}
