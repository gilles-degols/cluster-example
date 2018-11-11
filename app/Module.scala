import com.google.inject.AbstractModule
import net.degols.example.cluster.LeaderExample
import net.degols.example.cluster.example.Activity
import play.api.libs.concurrent.AkkaGuiceSupport

class Module extends AbstractModule with AkkaGuiceSupport {

  override def configure(): Unit = {
    bind(classOf[LifeCycle]).asEagerSingleton()
    bindActor[LeaderExample]("cluster-leader-example")
  }
}
