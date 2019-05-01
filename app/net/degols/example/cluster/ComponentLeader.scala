package net.degols.example.cluster

import javax.inject.{Inject, Singleton}
import net.degols.example.cluster.activity.ActivityLeader
import net.degols.example.cluster.activity.loadbalancing.BasicFilesgateLoadBalancer
import net.degols.libs.cluster.balancing.LoadBalancer
import net.degols.libs.cluster.manager.{ComponentLeaderApi, PackageLeaderApi}

@Singleton
class ComponentLeader @Inject()(activityLeader: ActivityLeader) extends ComponentLeaderApi {
  override def componentName: String = "Component"

  override def packageLeaders: Seq[PackageLeaderApi] = List(activityLeader)

  override def loadBalancers: Seq[LoadBalancer] = {
    val balancer = new BasicFilesgateLoadBalancer()
    List(balancer)
  }
}
