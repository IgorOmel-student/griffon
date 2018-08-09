package \${groupId}

import griffon.core.artifact.GriffonController
import griffon.core.controller.ControllerAction
import griffon.annotations.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.application.threading.Threading
import griffon.annotations.core.Nonnull

@ArtifactProviderFor(GriffonController)
class AppController {
    @MVCMember @Nonnull
    AppModel model

    @ControllerAction
    @Threading(Threading.Policy.INSIDE_UITHREAD_ASYNC)
    void click() {
        model.clickCount++
    }
}