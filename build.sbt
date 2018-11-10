import play.sbt.PlayScala
import sbt.RootProject

name := "cluster-example"
 
version := "0.0.1"

scriptClasspath in bashScriptDefines ~= (cp => "/../../../../../../../../../../etc/net.degols/local:/../../../../../../../../../../usr/lib/net.degols/election/conf/application.conf" +: cp)

scalacOptions ++= Seq("-deprecation", "-feature", "-language:postfixOps")

lazy val `filesgate` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"
lazy val playVersion = "2.6.1"
lazy val akkaVersion = "2.5.2"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq( jdbc , ehcache , ws , specs2 % Test , guice )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

// Cluster library
val clusterLibraryVersion = "0.0.1"
val clusterPath = "../cluster"
lazy val clusterLibrary: RootProject = RootProject(file(clusterPath))
val useLocalClusterLibrary = true
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

// Akka Remoting
libraryDependencies += "com.typesafe.akka" %% "akka-remote" % akkaVersion

