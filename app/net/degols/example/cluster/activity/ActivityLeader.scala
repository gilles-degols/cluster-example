package net.degols.example.cluster.activity

import akka.actor.{ActorContext, ActorRef, Props}
import javax.inject.{Inject, Singleton}
import net.degols.example.cluster.activity.example.Activity
import net.degols.example.cluster.activity.loadbalancing.BasicFilesgateLoadBalancerType
import net.degols.libs.cluster.balancing.BasicLoadBalancerType
import net.degols.libs.cluster.manager.{PackageLeaderApi, StartWorkerWrapper, WorkerInfo}
import net.degols.libs.cluster.messages.{JVMInstance, WorkerTypeInfo}

@Singleton
class ActivityLeader extends PackageLeaderApi {
  override val packageName: String = "Activity"

  override def setupWorkers(): Unit = {
    setWorker(
      WorkerInfo("Activity", Option(BasicLoadBalancerType(instances = 2, JVMInstance))),
      work => _context.actorOf(Props.create(classOf[Activity]), name = work.actorName)
    )

    setWorker(
     WorkerInfo("ActivityCustom", Option(BasicFilesgateLoadBalancerType(instances = 5, JVMInstance))),
      work => _context.actorOf(Props.create(classOf[Activity]), name = work.actorName)
    )
  }
}
