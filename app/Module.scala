import com.google.inject.AbstractModule
import net.degols.example.cluster.ComponentLeader
import net.degols.libs.cluster.manager.{ClusterLeaderActor, ComponentLeaderApi}
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bind(classOf[LifeCycle]).asEagerSingleton()

    bind(classOf[ComponentLeaderApi]).to(classOf[ComponentLeader])
    bindActor[ClusterLeaderActor]("cluster-leader-actor")
  }
}
