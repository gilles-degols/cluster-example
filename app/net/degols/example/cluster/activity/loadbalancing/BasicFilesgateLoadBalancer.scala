package net.degols.example.cluster.activity.loadbalancing

import net.degols.libs.cluster.balancing.LoadBalancer
import net.degols.libs.cluster.configuration.{ClusterConfiguration, ClusterConfigurationApi}
import net.degols.libs.cluster.core.{Node, Worker, WorkerManager, WorkerType}
import net.degols.libs.cluster.messages._
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

/**
  * Basic example of a custom load balancer. In this case, we only took the code of the BasicLoadBalancer from the cluster
  * library and put it in another name to gave a simple example
  * @param instances
  * @param instanceType
  */
@SerialVersionUID(1L)
case class BasicFilesgateLoadBalancerType(instances: Int, instanceType: InstanceType = ClusterInstance) extends LoadBalancerType {
  override def toString: String = {
    val location = if(instanceType == JVMInstance) "jvm" else "cluster"
    s"BasicFilesgateLoadBalancer: $instances instances/$location"
  }
}

class BasicFilesgateLoadBalancer extends LoadBalancer {
  private val logger = LoggerFactory.getLogger(getClass)

  override def isLoadBalancerType(loadBalancerType: LoadBalancerType): Boolean = loadBalancerType.isInstanceOf[BasicFilesgateLoadBalancerType]

  override def hardWorkDistribution(workerType: WorkerType, order: WorkerTypeOrder)(implicit ec: ExecutionContext): Future[Unit] = {
    Future{
      logger.debug("BasicFilesgateLoadBalancer - There is no hard work distribution in the BasicFilesgateLoadBalancer.")
    }
  }

  override def softWorkDistribution(workerType: WorkerType, order: WorkerTypeOrder)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      logger.debug(s"BasicFilesgateLoadBalancer - Soft work distribution for ${workerType.id}")
      val nodes = clusterManagement.cluster.nodesForWorkerType(workerType)
      val balancerType = order.loadBalancerType.asInstanceOf[BasicFilesgateLoadBalancerType]

      if(nodes.isEmpty) {
        logger.error(s"The WorkerType $workerType has no nodes available, no work distribution possible.")
      } else {
        // Depending on the type of WorkType, we want to create a specific number of workers by JVM or per cluster
        if(balancerType.instanceType == JVMInstance) {
          softWorkDistributionPerJVM(workerType, nodes, balancerType, order)
        } else {
          softWorkDistributionPerCluster(workerType, nodes, balancerType, order)
        }
      }
    }
  }

  private def softWorkDistributionPerJVM(workerType: WorkerType, nodes: Seq[Node], balancerType: BasicFilesgateLoadBalancerType, order: WorkerTypeOrder): Unit = {
    val wantedInstances = balancerType.instances

    nodes.flatMap(_.workerManagers.filter(_.isUp))
      .foreach(workerManager => {
        val runningInstances = workerManager.workerTypes.find(_ == workerType).get.workers.filter(_.isUp)

        var i = runningInstances.size
        if(i < wantedInstances) {
          logger.info(s"Starting ${wantedInstances - i} instances of $workerType on $this")
        }
        while(i < wantedInstances) {
          workerManager.startWorker(context, workerType, order)
          i += 1
        }
      })
  }

  private def softWorkDistributionPerCluster(workerType: WorkerType, nodes: Seq[Node], balancerType: BasicFilesgateLoadBalancerType, order: WorkerTypeOrder): Unit = {
    val wantedInstances = balancerType.instances

    val managerAndRunningInstances: Map[WorkerManager, Seq[Worker]] = nodes.flatMap(node => node.workerManagers.filter(_.isUp))
      .map(workerManager => workerManager -> workerManager.workerTypes.filter(_ == workerType).flatMap(_.workers.filter(_.isUp))).toMap
    val runningInstances = managerAndRunningInstances.values.flatten.size
    if(managerAndRunningInstances.keys.isEmpty) {
      logger.warn(s"There is no WorkerManager available for $workerType, not possible to start the missing ${wantedInstances - wantedInstances} instances.")
    } else if(runningInstances < wantedInstances) {
      logger.info(s"Starting ${wantedInstances - runningInstances} instances of $workerType on various WorkerManagers.")
      // We try to distribute the load between managers. For now we simply choose managers at random (but those having less than the average number of instances)
      val averageWantedInstances = (wantedInstances + 1f) / managerAndRunningInstances.keys.size
      val availableManagers = managerAndRunningInstances.toList.filter(_._2.size < averageWantedInstances)
      if(availableManagers.isEmpty) {
        logger.error(s"No good WorkerManager found for $workerType...")
      } else {
        var i = runningInstances
        while(i < wantedInstances) {
          val workerManager = Random.shuffle(availableManagers).head
          workerManager._1.startWorker(context, workerType, order)
          i += 1
        }
      }
    }
  }
}
