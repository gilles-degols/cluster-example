package net.degols.example.cluster

import javax.inject.{Inject, Singleton}
import net.degols.example.cluster.activity.ActivityLeader
import net.degols.example.cluster.activity.loadbalancing.BasicFilesgateLoadBalancer
import net.degols.libs.cluster.balancing.LoadBalancer
import net.degols.libs.cluster.manager.{ComponentLeaderApi, PackageLeaderApi}

@Singleton
class ComponentLeader @Inject()(activityLeader: ActivityLeader) extends ComponentLeaderApi {
  override val componentName: String = "Component"

  override val packageLeaders: Seq[PackageLeaderApi] = List(activityLeader)

  override val loadBalancers: Seq[LoadBalancer] = {
    val balancer = new BasicFilesgateLoadBalancer()
    List(balancer)
  }
}
