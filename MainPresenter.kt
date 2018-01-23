
import co.riseapps.modasti.domain.interactor.AuthInteractor
import co.riseapps.modasti.domain.model.AuthUser
import co.riseapps.modasti.presentation.ui.base.RxDisposablePresenter
import co.riseapps.modasti.presentation.ui.helper.operation.OperationView
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainPresenter : RxDisposablePresenter<MainPresenter.View>() {

    @Inject lateinit var authInteractor: AuthInteractor

    private lateinit var operationView: OperationView<*>

    override fun onTakeView(view: View?) {
        super.onTakeView(view)
        operationView = view?.operationView()!!

        add(authInteractor
                .user()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { view.fillUserData(it) },
                        { Timber.e(it) }))
    }

    fun openHome() = view?.replaceFragment(MainActivity.HOME, false)

    fun openBrowse()  = view?.replaceFragment(MainActivity.BROWSE, false)

    fun openInspire()  = view?.replaceFragment(MainActivity.INSPIRE, false)

    fun openProfile()  = view?.replaceFragment(MainActivity.PROFILE, false)

    fun confirmLogout() = view?.confirmLogout()

    fun performLogout() {
        add(authInteractor
                .logout()
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { operationView.showProgress() }
                .doFinally {
                    operationView.hideProgress()
                    view?.onLogoutComplete()
                }.subscribe({}, { Timber.e(it) }))
    }

    interface View{

        fun fillUserData(authUser: AuthUser)

        fun replaceFragment(key: String, addBackStack: Boolean = true)

        fun addFragment(key: String, addBackStack: Boolean = true)

        fun confirmLogout()

        fun onLogoutComplete()

        fun operationView(): OperationView<*>
    }
}
