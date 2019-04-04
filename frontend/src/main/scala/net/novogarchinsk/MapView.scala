package net.novogarchinsk

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

case class region(latitude:Double,longitude:Double,latitudeDelta:Double,longitudeDelta:Double)

@react object MapView extends ExternalComponent {
  case class Props(
                    style: js.UndefOr[js.Object] = js.undefined,
                    region: js.UndefOr[region] = js.undefined)

  @JSImport("react-native-maps", JSImport.Default)
  @js.native
  object ReactNativeMaps extends js.Object

  override val component = ReactNativeMaps
}
