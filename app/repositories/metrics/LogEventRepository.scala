package repositories.metrics
import model.domain.metrics.LogEvent
import model.infra.metrics.LogEvents

class LogEventRepository extends MetricsRepository[LogEvent, LogEvents](LogEvents.query)
