package net.degols.example.cluster.activity

import akka.actor.{ActorContext, ActorRef, Props}
import javax.inject.Inject
import net.degols.example.cluster.activity.example.Activity
import net.degols.example.cluster.activity.loadbalancing.BasicFilesgateLoadBalancerType
import net.degols.libs.cluster.balancing.BasicLoadBalancerType
import net.degols.libs.cluster.manager.{PackageLeaderApi, WorkerInfo}
import net.degols.libs.cluster.messages.{JVMInstance, WorkerTypeInfo}

class ActivityLeader extends PackageLeaderApi {
  override def packageName: String = "Activity"

  override def startWorker(shortName: String, actorName: String): ActorRef = {
    shortName match {
      case "Activity" =>
        val worker = _context.actorOf(Props.create(classOf[Activity]), name = actorName)
        worker ! "Super Stuff!"
        //context.system.scheduler.scheduleOnce(30 seconds, worker, Kill)
        worker
      case "ActivityCustom" =>
        val worker = _context.actorOf(Props.create(classOf[Activity]), name = actorName)
        worker ! "Super custom stuff!"
        worker
      case _ => throw new Exception(s"Invalid short-name received: $shortName")
    }
  }

  override def workerInfos: Seq[WorkerInfo] = {
    List(
      WorkerInfo("Activity", Option(BasicLoadBalancerType(instances = 2, JVMInstance))),
      WorkerInfo("ActivityCustom", Option(BasicFilesgateLoadBalancerType(instances = 5, JVMInstance)))
    )
  }
}
