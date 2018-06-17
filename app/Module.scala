import com.google.inject.AbstractModule
import services.AgeConnectionService

class Module extends AbstractModule {

  override def configure() = {
    bind(classOf[AgeConnectionService])
  }


}
