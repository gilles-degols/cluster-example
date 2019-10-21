import play.sbt.PlayScala
import sbt.RootProject

name := "cluster-example"
version := "1.0.0"

scriptClasspath in bashScriptDefines ~= (cp => "/../../../../../../../../../../etc/net.degols/local:/../../../../../../../../../../usr/lib/net.degols/election/conf/application.conf" +: cp)

lazy val `filesgate` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"
val clusterLibraryVersion = "1.1.0"
val useLocalClusterLibrary = true

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

// Cluster library
val clusterPath = "../cluster"
lazy val clusterLibrary: RootProject = RootProject(file(clusterPath))
val localClusterAvailable = scala.reflect.io.File(scala.reflect.io.Path(clusterPath)).exists
lazy val proj = if(localClusterAvailable && useLocalClusterLibrary) {
  (project in file(".")).enablePlugins(PlayScala).dependsOn(clusterLibrary)
} else {
  (project in file(".")).enablePlugins(PlayScala)
}

lazy val clusterDependency = if(localClusterAvailable && useLocalClusterLibrary) {
  Seq()
} else {
  Seq("net.degols" %% "cluster" % clusterLibraryVersion exclude("log4j", "log4j") exclude("org.slf4j","slf4j-log4j12"))
}
libraryDependencies ++= clusterDependency

