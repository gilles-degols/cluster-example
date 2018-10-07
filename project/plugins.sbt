logLevel := Level.Warn

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.17")

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "latest.integration")