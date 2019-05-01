import javax.inject._

import akka.actor.ActorRef
import play.api.Logger
import play.api.inject.ApplicationLifecycle

import scala.concurrent.Future

@Singleton
class LifeCycle @Inject()(lifecycle: ApplicationLifecycle, @Named("cluster-leader-actor") main: ActorRef) {
  lifecycle.addStopHook { () =>
    Logger.error("Main Life Cycle Off")
    Future.successful(Unit)
  }
}