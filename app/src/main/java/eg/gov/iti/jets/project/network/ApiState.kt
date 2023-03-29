package eg.gov.iti.jets.project.network

import eg.gov.iti.jets.project.model.Root


sealed class ApiState {
    class Success(val data: Root):ApiState()
    class Failure(val msg:Throwable):ApiState()
    object Loading:ApiState()

}