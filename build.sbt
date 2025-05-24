lazy val akkaHttpVersion = "10.7.1"
lazy val akkaVersion    = "2.10.5"
resolvers += "Akka library repository".at("https://repo.akka.io/maven")

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.example",
      scalaVersion    := "3.3.4"
    )),
    name := "akka-storage",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka" %% "akka-pki"                 % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.5.17",
      "com.auth0"         % "java-jwt"                  % "4.5.0",

      // "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      // "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      // "org.scalatest"     %% "scalatest"                % "3.2.12"        % Test
    ),
    ThisBuild / assemblyMergeStrategy := {
      case PathList("META-INF", "versions", "9", "module-info.class") => MergeStrategy.discard
      case PathList("module-info.class") => MergeStrategy.discard
      case x =>
        val oldStrategy = (ThisBuild / assemblyMergeStrategy).value
        oldStrategy(x)
    }
  )
  // .enablePlugins(DockerPlugin)
