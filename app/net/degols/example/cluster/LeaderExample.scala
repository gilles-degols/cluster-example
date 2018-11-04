package net.degols.example.cluster

import java.io.File

import akka.actor.{Actor, ActorRef, Props}
import net.degols.filesgate.libs.election.{ConfigurationService, ElectionService, ElectionWrapper}
import org.slf4j.LoggerFactory
import javax.inject.{Inject, Singleton}

import com.typesafe.config.{Config, ConfigFactory}
import net.degols.example.cluster.example.Activity
import net.degols.filesgate.libs.cluster.ClusterConfiguration
import net.degols.filesgate.libs.cluster.core.Cluster
import net.degols.filesgate.libs.cluster.manager.{Manager, WorkerLeader}
import net.degols.filesgate.libs.cluster.messages.{BasicLoadBalancerType, WorkerTypeInfo}

@Singleton
class LeaderExample @Inject()(electionService: ElectionService, configurationService: ConfigurationService, clusterConfiguration: ClusterConfiguration, cluster: Cluster)
  extends WorkerLeader(electionService, configurationService, clusterConfiguration, cluster){

  private val logger = LoggerFactory.getLogger(getClass)
  override def receive: Receive = {
    case message =>
      logger.debug(s"[LeaderExample] Received unknown message: $message")
  }

  /**
    * Class to implement by the developer.
    *
    * @param workerTypeId
    */
  override def startWorker(workerTypeId: String, actorName: String): ActorRef = {
    workerTypeId match {
      case "Activity" =>
        context.actorOf(Props.create(classOf[Activity]), name = actorName)
      case _ => throw new Exception(s"Invalid WorkerTypeId received: $workerTypeId")
    }
  }

  /**
    * List of available WorkerActors given by the developer in the current jvm.
    */
  override def allWorkerTypeInfo: List[WorkerTypeInfo] = {
    List(
      WorkerTypeInfo(self, "Activity", BasicLoadBalancerType(instances = 1))
    )
  }
}
