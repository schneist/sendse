resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.3")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.27")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.3")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject"      % "0.6.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.12")
addSbtPlugin( "com.vmunier" % "sbt-web-scalajs" % "1.0.8-0.6")
addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.14.0")
addSbtPlugin("org.jetbrains" % "sbt-idea-compiler-indices" % "0.1.3")