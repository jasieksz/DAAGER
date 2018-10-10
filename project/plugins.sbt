logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.19")
addSbtPlugin("com.iheart" %% "sbt-play-swagger" % "0.7.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.9")
