name := "daager"

version := "0.2"

lazy val `daager` = (project in file("."))
  .enablePlugins(
    PlayScala,
    SwaggerPlugin,
    JavaAppPackaging,
    DockerPlugin
  )
  .settings(
    watchSources ++= (baseDirectory.value / "public/ui" ** "*").get
  )

swaggerDomainNameSpaces := Seq("model", "actors", "controllers")

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.7"
scalacOptions += "-Ypartial-unification"

fork := true

libraryDependencies ++= Seq(ehcache, ws, specs2 % Test, guice, filters)
libraryDependencies += "org.typelevel" %% "cats-core" % "1.1.0"
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3",
  "com.typesafe.play" %% "play-json-joda" % "2.6.9",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "org.postgresql" % "postgresql" % "42.2.2",
  "com.google.inject" % "guice" % "4.2.0"
)

libraryDependencies += "com.github.tminglei" %% "slick-pg" % "0.16.3"
libraryDependencies += "com.github.tminglei" %% "slick-pg_joda-time" % "0.16.3"

// JAVA 9 Fixes
libraryDependencies ++= Seq(
  "javax.xml.bind" % "jaxb-api" % "2.3.0",
  "javax.annotation" % "javax.annotation-api" % "1.3.2",
  "javax.el" % "javax.el-api" % "3.0.0",
  "org.glassfish" % "javax.el" % "3.0.0",
)

libraryDependencies += "org.webjars" % "swagger-ui" % "2.2.0"

libraryDependencies += "com.appnexus.grafana-client" % "grafana-api-java-client" % "1.0.5"

// task settings
dockerExposedPorts := Seq(9000)

// stage settings
javaOptions in Universal ++= Seq(
  "-Dplay.evolutions.db.default.autoApply=true",
  "-Dslick.dbs.default.db.url=jdbc:postgresql://db:5432/daager"
)

// scalaFmt
scalafmtOnCompile := true
