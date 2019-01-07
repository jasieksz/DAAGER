logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin"          % "2.6.20")
addSbtPlugin("com.iheart"        %% "sbt-play-swagger"   % "0.7.5")
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager" % "1.3.9")
addSbtPlugin("com.geirsson"      % "sbt-scalafmt"        % "1.5.1")
