package definitions

import dsl.elements.OperationDefinition
import johnnyfivescalajs.JohnnyFive.{Board, _}
import org.reactivestreams.{Publisher, Subscriber, Subscription}

import scala.scalajs.js.Date

object J5Definitions {

  type PinState =  Product2[Int,Boolean]

  def switchLED = new OperationDefinition[PinState , Boolean ,(PinState,Board) => Boolean ](
    (in, board) => {
      board.digitalWrite(in._1,if( in._2) 1 else 0)
      !in._2
    }
  )

  type SubscriberCreator[In,Out] = (In,Board) => Subscriber[Out]

  type Pin_BlinkLength = (Int,Double)

  def sinkBlinkLEDLength :OperationDefinition[Pin_BlinkLength , Double ,SubscriberCreator[Pin_BlinkLength,Double]] =
    new OperationDefinition[Pin_BlinkLength , Double ,(Pin_BlinkLength,Board) => Subscriber[Double] ](
      (in, _) => {
        val led =  Led(new LedOption { pin = in._1.toDouble})
        val s = new Subscriber[Double] {
          override def onSubscribe(s: Subscription): Unit = {}

          override def onNext(elem: Double): Unit = {
            led.pulse(elem)
          }

          override def onError(ex: Throwable): Unit = {led.blink(110.1)}

          override def onComplete(): Unit = led.off()
        }
        s.onNext(in._2)
        s
      }
    )

  def waitForButton =
    new OperationDefinition[Int, Unit ,(Int,Board,Subscriber[Unit]) => Unit ]((pin:Int,b :Board, cb:Subscriber[Unit]) =>{
      Button(pin)
        .on("hold", () => {
          cb.onNext()
          cb.onComplete
        })
    })

  type PublisherCreator[In,Out] = (In,Board) => Publisher[Out]

  def sourceButtonDownLength =
    new OperationDefinition[Int, Double ,PublisherCreator[Int,Double]]((pinI:Int,_)=> {
      (subscriber: Subscriber[_ >: Double]) => {
        val b = Button(new ButtonOption {pin = pinI;isPullup = true})
        var downtime :Double = Date.now()
        b.on("down", () => {
          downtime = Date.now()
        })
        b.on("up", () => {
          subscriber.onNext( Date.now() - downtime )
        })
      }
    })

  type Pin_SensorType =  Product2[Int,String]

  def sourceTemperatureSensor =
    new OperationDefinition[Pin_SensorType, Double ,PublisherCreator[Pin_SensorType,Double] ]((p: Pin_SensorType, _)=> {
      (subscriber: Subscriber[_ >: Double]) => {
        val h = Multi(new MultiOption {controller = p._2})
          h.on("change", () => {
            subscriber.onNext(h.thermometer.celsius);
          })
      }
    })

}