
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import co.riseapps.modasti.ModastiApp
import co.riseapps.modasti.R
import co.riseapps.modasti.domain.model.AuthUser
import co.riseapps.modasti.presentation.di.component.ActivityComponent
import co.riseapps.modasti.presentation.di.component.FragmentComponent
import co.riseapps.modasti.presentation.di.module.ActivityModule
import co.riseapps.modasti.presentation.ui.base.DaggerNucleusAppCompatActivity
import co.riseapps.modasti.presentation.ui.base.DaggerNucleusSupportFragment
import co.riseapps.modasti.presentation.ui.base.RxDisposablePresenter
import co.riseapps.modasti.presentation.ui.browse.BrowseFragment
import co.riseapps.modasti.presentation.ui.helper.adapter.SearchAdapter
import co.riseapps.modasti.presentation.ui.helper.hideKeyboard
import co.riseapps.modasti.presentation.ui.helper.not_implemented.NotImplementedYetFragment
import co.riseapps.modasti.presentation.ui.helper.operation.CompositeOperationView
import co.riseapps.modasti.presentation.ui.helper.operation.OperationView
import co.riseapps.modasti.presentation.ui.helper.operation.view.ErrorSnackBarView
import co.riseapps.modasti.presentation.ui.helper.operation.view.ProgressBarView
import co.riseapps.modasti.presentation.ui.helper.operation.view.SuccessSnackBarView
import co.riseapps.modasti.presentation.ui.home.HomeFragment
import co.riseapps.modasti.presentation.ui.inspire.InspireFragment
import co.riseapps.modasti.presentation.ui.login.LoginActivity
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_toolbar.*
import nucleus.factory.RequiresPresenter
import com.pinterest.android.pdk.PDKClient;

@RequiresPresenter(MainPresenter::class)
class MainActivity : DaggerNucleusAppCompatActivity<MainPresenter, ActivityComponent>(),
        NavigationView.OnNavigationItemSelectedListener, MainPresenter.View {

    override val presenterComponent: ActivityComponent
        get() = ModastiApp.appComponent.plus(ActivityModule(this))

    private var currentItemId = -1
    private var currFragment = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = object : ActionBarDrawerToggle(this, draweLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                hideKeyboard()
            }
        }
        draweLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false
        toggle.setHomeAsUpIndicator(R.drawable.ic_burgermenu)
        toggle.setToolbarNavigationClickListener {
            draweLayout.openDrawer(GravityCompat.START)
        }
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onResume() {
        super.onResume()

        if (currentItemId == -1) {
            currentItemId = R.id.nav_home
            presenter.openHome()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.home_menu, menu)

        val item = menu!!.findItem(R.id.search)
        searchView.setMenuItem(item)
        val searchAdapter = SearchAdapter(this, resources.getStringArray(R.array.query_suggestions))
        searchView.setAdapter(searchAdapter)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        when {
            draweLayout.isDrawerOpen(GravityCompat.START) -> draweLayout.closeDrawer(GravityCompat.START)
            searchView.isSearchOpen -> searchView.closeSearch()
            else -> super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        draweLayout.closeDrawer(GravityCompat.START)
        val itemId = item.itemId

        if (R.id.nav_logout == itemId) {
            presenter.confirmLogout()
            return false
        }

        if (currentItemId == itemId) return true

        currentItemId = itemId
        openMenuItemById(currentItemId)

        return true
    }

    private fun openMenuItemById(id: Int) {
        when (id) {
            R.id.nav_home -> presenter.openHome()
            R.id.nav_browse -> presenter.openBrowse()
            R.id.nav_inspire -> presenter.openInspire()
            R.id.nav_profile -> presenter.openProfile()
        }
    }

    private fun setToolbarText(@StringRes id: Int) {
        supportActionBar?.title = getString(id)
    }

    override fun replaceFragment(key: String, addBackStack: Boolean) {
        val fragment = getFragmentByKey(key)
        currFragment = key
        val commit = supportFragmentManager.beginTransaction()
                .replace(R.id.main_container, fragment, key)

        if (addBackStack)
            commit.addToBackStack(key)

        commit.commitAllowingStateLoss()

    }

    override fun addFragment(key: String, addBackStack: Boolean) {
        val fragment = getFragmentByKey(key)
        currFragment = key
        val commit = supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, fragment, key)

        if (addBackStack)
            commit.addToBackStack(key)

        commit.commitAllowingStateLoss()
    }

    private fun getFragmentByKey(key: String): DaggerNucleusSupportFragment<out RxDisposablePresenter<out Any>, FragmentComponent> =
            when (key) {
                HOME -> {
                    setToolbarText(R.string.home_nav_title)
                    HomeFragment.newInstance()
                }
                BROWSE -> {
                    setToolbarText(R.string.browse_nav_title)
                    BrowseFragment.newInstance()
                }
                INSPIRE -> {
                    setToolbarText(R.string.inspire_nav_title)
                    InspireFragment.newInstance()
                }
                PROFILE -> {
                    setToolbarText(R.string.profile_nav_title)
                    NotImplementedYetFragment.newInstance()
                }
                else -> throw IllegalArgumentException("No screen found")
            }

    override fun confirmLogout() {
        val dialog = AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirm_logout_message))
                .setPositiveButton(android.R.string.yes) { _, _ -> presenter.performLogout() }
                .setNegativeButton(android.R.string.no, null)
                .create()
        dialog.show()
    }

    override fun onLogoutComplete() {
        LoginActivity.start(this)
        finish()
    }

    override fun fillUserData(authUser: AuthUser) {
        val header = navigationView.getHeaderView(0)
        header.findViewById<CircleImageView>(R.id.userAvatar).setImageResource(R.drawable.img_avatar)
        header.findViewById<TextView>(R.id.userNameTv).text = authUser.userName
    }

    override fun operationView(): OperationView<*> {
        return CompositeOperationView(ProgressBarView(this, getString(R.string.wait_progress), false),
                SuccessSnackBarView(main_container),
                ErrorSnackBarView(main_container))
    }

    override fun onNewIntent(intent: Intent) {
        if (intent.hasExtra(INSPIRE)) {
            navigationView.setCheckedItem(R.id.nav_inspire)
            currentItemId = R.id.nav_inspire
            presenter.openInspire()
        }
    }

    companion object {

        const val HOME = "home"
        const val BROWSE = "browse"
        const val INSPIRE = "inspire"
        const val PROFILE = "profile"

        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        fun startInspire(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(INSPIRE, true))
        }
    }
}
