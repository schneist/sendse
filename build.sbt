import sbt.Keys.scalaVersion
import sbtcrossproject.CrossPlugin.autoImport.{CrossType, crossProject}

import scala.sys.process._

ThisBuild /  organization := "net.novogarchinsk"
ThisBuild / version      := "0.3.0-SNAPSHOT"
ThisBuild / scalaVersion := "2.12.8"


ideaPort in Global := 65337

val circeVersion = "0.11.1"
val scalaTestVersion = "3.0.8"

lazy val dsl =  crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full).in(file("""./iotte""")).
  settings(
    name := "IoTTE",

    scalacOptions ++= Seq(
      "-feature",
      "-Ypartial-unification" ,
    ),

    resolvers ++= Seq(
      "Typesafe" at "http://repo.typesafe.com/typesafe/releases/",
      "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
      "Sonatype OSS Public" at "https://oss.sonatype.org/content/repositories/public",
      "JFrog" at  "http://repo.jfrog.org/artifactory/libs-releases/",
      "JBoss" at "http://repository.jboss.org/nexus/content/groups/public-jboss/",
      "MVNSearch" at "http://www.mvnsearch.org/maven2/"
    ),

    publishMavenStyle := true,

    libraryDependencies += "io.monix" %%% "monix" % "3.0.0-RC2",
    libraryDependencies += "org.scalactic" %%% "scalactic" % "3.0.7",
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.7" % "test",
    libraryDependencies +="com.chuusai" %%% "shapeless" % "2.3.3",

  ).
  jvmSettings(

  ).
  jsSettings(
    resolvers += Resolver.sonatypeRepo("releases"),
    libraryDependencies += "io.scalajs" %%% "nodejs" % "0.4.2",
    libraryDependencies += "org.querki" %%% "querki-jsext" % "0.8",
    libraryDependencies += "net.novogarchinsk" %%% "johnny5scala-js" % "0.0.2",
    libraryDependencies += "fr.hmil" %%% "roshttp" % "2.2.3",
    libraryDependencies += "net.novogarchinsk" %%% "pyshell-scalajs" % "0.1",
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    skip in packageJSDependencies := false,
    scalaJSUseMainModuleInitializer := true,
    scalacOptions ++= Seq(
      "-P:scalajs:sjsDefinedByDefault"
    )
  )


lazy val frontend  = (project in file( "./frontend")).settings(
  name := "frontend",


  npmDependencies in Compile +=   "react-native-webview" -> "5.8.0",
  npmDependencies in Compile +=   "react-native" -> "0.59.5",


  libraryDependencies += "io.tmos" %% "arm4s" % "1.1.0",
  libraryDependencies += "me.shadaj" %%% "slinky-native" % "0.6.2",
  libraryDependencies += "me.shadaj" %%% "slinky-hot" % "0.6.2",
  libraryDependencies += "io.monix" %%% "monix" % "3.0.0-RC2",
  libraryDependencies += "com.github.karasiq" %%% "scalajs-highcharts" % "1.2.1",
  scalacOptions += "-P:scalajs:sjsDefinedByDefault",

  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full),

  scalaJSModuleKind := ModuleKind.CommonJSModule,

).enablePlugins(ScalaJSPlugin).enablePlugins(ScalaJSBundlerPlugin, ScalaJSWeb)




lazy val functions  = (project in file("./functions")).settings(

  name := "functions",

  scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },

  scalacOptions ++= Seq(
    "-P:scalajs:sjsDefinedByDefault",
    "-feature",
    "-deprecation",
    "-language:reflectiveCalls"
  ),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  skip in packageJSDependencies in Test := false,
  skip in packageJSDependencies in Compile := true,


  //webpackResources := baseDirectory.value * "*.js",
  pipelineStages in Assets := Seq(scalaJSPipeline),

  libraryDependencies ++= Seq(
    "org.scalactic" %%% "scalactic" % scalaTestVersion,
    "org.scalatest" %%% "scalatest" % scalaTestVersion % "test",
    "eu.timepit" %%% "refined" % "0.9.8",
    "me.shadaj" %%% "slinky-core" % "0.6.2",
    "me.shadaj" %%% "slinky-web" % "0.6.2",
    "org.scalaz" %%% "scalaz-zio" % "1.0-RC5",
    "com.github.mpilquist" %% "simulacrum" % "0.19.0"
  ),

  libraryDependencies += "io.scalajs.npm" %%% "express" % "4.14.1",

  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core",
    "io.circe" %%% "circe-generic",
    "io.circe" %%% "circe-parser",
    "io.circe" %%% "circe-shapes"

  ).map(_ % circeVersion),

  InputKey[Unit]("gcDeploy") := {
    val args :Seq[String] = sbt.complete.DefaultParsers.spaceDelimited("gcDeploy <project-id> <functionname>").parsed

    val projectId = args.head
    val functionName = args(1)
    val trigger = "--trigger-http"

    val region = "europe-west1"

    val gcTarget = target.value / "gcloud"
    val function = gcTarget / "function.js"
    sbt.IO.copyFile((fastOptJS in Compile).value.data, function)
    sbt.IO.copyDirectory((resourceDirectory in Compile).value, gcTarget)

    s"gcloud functions deploy $functionName --source ${gcTarget.getAbsolutePath} --stage-bucket sendsef $trigger --runtime nodejs8 --project $projectId --region $region"!
  },




).enablePlugins(ScalaJSPlugin)



