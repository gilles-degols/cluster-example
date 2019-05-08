package net.degols.example.cluster

import javax.inject.{Inject, Singleton}
import net.degols.example.cluster.activity.ActivityLeader
import net.degols.example.cluster.activity.loadbalancing.BasicFilesgateLoadBalancer
import net.degols.libs.cluster.balancing.LoadBalancer
import net.degols.libs.cluster.configuration.ClusterConfigurationApi
import net.degols.libs.cluster.manager.{ComponentLeaderApi, PackageLeaderApi}

import scala.concurrent.Future

@Singleton
class ComponentLeader @Inject()(activityLeader: ActivityLeader, clusterConfiguration: ClusterConfigurationApi) extends ComponentLeaderApi {
  implicit val ec = clusterConfiguration.executionContext

  override val componentName: String = "Component"

  override val packageLeaders: Future[Seq[PackageLeaderApi]] = Future{List(activityLeader)}

  override val loadBalancers: Future[Seq[LoadBalancer]] = {
    Future {
      val balancer = new BasicFilesgateLoadBalancer()
      List(balancer)
    }
  }
}
