import javax.inject._
import akka.actor.ActorRef
import net.degols.libs.cluster.utils.Logging
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

@Singleton
class LifeCycle @Inject()(lifecycle: ApplicationLifecycle, @Named("cluster-leader-actor") main: ActorRef) extends Logging{

  lifecycle.addStopHook { () =>
    error("Main Life Cycle Off")
    Future.successful(Unit)
  }
}