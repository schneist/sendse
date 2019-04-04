package net.novogarchinsk

import slinky.core.ExternalComponent
import slinky.core.annotations.react

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport


case class source (uri :String)

@react object WebView extends ExternalComponent {
  case class Props(source:  js.UndefOr[source] = js.undefined,
                   style: js.UndefOr[js.Object] = js.undefined)

  @JSImport("react-native-webview", "WebView")
  @js.native
  object ReactNativeWebview extends js.Object

  override val component = ReactNativeWebview
}
