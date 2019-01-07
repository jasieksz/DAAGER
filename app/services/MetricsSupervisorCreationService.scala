package services
import actors.MetricsSupervisor
import akka.actor.Props
import javax.inject.Inject

class MetricsSupervisorCreationService @Inject()(
  workersCreationService: WorkersCreationService
) {

  def getSupervisorProps(clusterAlias: String): Props = {
    MetricsSupervisor.props(
      clusterAlias,
      workersCreationService
    )
  }

}
