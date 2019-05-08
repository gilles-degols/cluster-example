package net.degols.example.cluster.activity.example

import akka.actor.Actor
import org.slf4j.LoggerFactory
import scala.concurrent.duration._

class Activity extends Actor{
  private val logger = LoggerFactory.getLogger(getClass)
  implicit val ac = context.system.dispatcher
  context.system.scheduler.schedule(1 seconds, 10 seconds, self, "ScheduledMessage")

  override def receive: Receive = {
    case x =>
      logger.debug(s"Received a message in the Activity actor: $x")

  }
}
