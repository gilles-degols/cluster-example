package net.degols.example.cluster.example

import akka.actor.Actor
import org.slf4j.LoggerFactory


class Activity extends Actor{
  private val logger = LoggerFactory.getLogger(getClass)
  println(s"-----> Current Activity: ${akka.serialization.Serialization.serializedActorPath(self)}")

  override def receive: Receive = {
    case x =>
      logger.debug(s"Received a message in the Activity actor: $x")
  }
}
