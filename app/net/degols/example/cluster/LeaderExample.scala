package net.degols.example.cluster

import java.io.File

import akka.actor.{Actor, ActorRef, Kill, Props}
import net.degols.filesgate.libs.election.{ConfigurationService, ElectionService, ElectionWrapper}
import org.slf4j.LoggerFactory
import javax.inject.{Inject, Singleton}

import scala.concurrent.duration._
import com.typesafe.config.{Config, ConfigFactory}
import net.degols.example.cluster.example.Activity
import net.degols.filesgate.libs.cluster.{ClusterConfiguration, Tools}
import net.degols.filesgate.libs.cluster.core.Cluster
import net.degols.filesgate.libs.cluster.manager.{Manager, WorkerLeader}
import net.degols.filesgate.libs.cluster.messages.{BasicLoadBalancerType, JVMInstance, WorkerTypeInfo}
import play.api.libs.concurrent.InjectedActorSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try

@Singleton
class LeaderExample @Inject()(electionService: ElectionService, configurationService: ConfigurationService, clusterConfiguration: ClusterConfiguration, cluster: Cluster)
  extends WorkerLeader(electionService, configurationService, clusterConfiguration, cluster) with InjectedActorSupport{
  context.system.scheduler.schedule(10 seconds, 10 seconds, self, "DEBUG")

  private val logger = LoggerFactory.getLogger(getClass)
  override def receive: Receive = {
    case "DEBUG" =>
      logger.debug(s"[LeaderExample] Cluster topology: \n${cluster}")
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
        val worker = context.actorOf(Props.create(classOf[Activity]), name = actorName)
        worker ! "Super Stuff!"
        //context.system.scheduler.scheduleOnce(30 seconds, worker, Kill)
        worker
      case _ => throw new Exception(s"Invalid WorkerTypeId received: $workerTypeId")
    }
  }

  /**
    * List of available WorkerActors given by the developer in the current jvm.
    */
  override def allWorkerTypeInfo: List[WorkerTypeInfo] = {
    List(
      WorkerTypeInfo(self, "Activity", BasicLoadBalancerType(instances = 2, JVMInstance))
    )
  }
}
