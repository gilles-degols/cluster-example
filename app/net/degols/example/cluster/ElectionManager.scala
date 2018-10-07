package net.degols.example.cluster

import java.io.File

import akka.actor.Actor
import net.degols.filesgate.libs.election.{ConfigurationService, ElectionService, ElectionWrapper}
import org.slf4j.LoggerFactory
import javax.inject.{Inject, Singleton}

import com.typesafe.config.{Config, ConfigFactory}

@Singleton
class ElectionManager @Inject()(electionService: ElectionService, configurationService: ConfigurationService) extends ElectionWrapper(electionService, configurationService){
  private val logger = LoggerFactory.getLogger(getClass)
  override def receive: Receive = {
    case message =>

      logger.debug(s"[ElectionManager] Received unknown message: $message")
  }
}
