/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.projectpackagehunter.views.detail

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import github.nisrulz.packagehunter.PackageHunter
import github.nisrulz.projectpackagehunter.R
import github.nisrulz.projectpackagehunter.modal.ElementInfo
import kotlinx.android.synthetic.main.activity_detail.imgvw_icn
import kotlinx.android.synthetic.main.activity_detail.rv_detaillist
import kotlinx.android.synthetic.main.activity_detail.txtvw_firsttime
import kotlinx.android.synthetic.main.activity_detail.txtvw_lastupdated
import kotlinx.android.synthetic.main.activity_detail.txtvw_pkgname
import kotlinx.android.synthetic.main.activity_detail.txtvw_vc
import kotlinx.android.synthetic.main.activity_detail.txtvw_vname
import kotlinx.android.synthetic.main.toolbar.toolbar
import java.util.ArrayList
import java.util.Locale


class DetailActivity : AppCompatActivity() {

    private lateinit var packageHunter: PackageHunter
    private lateinit var pkgNameOfApp: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(github.nisrulz.projectpackagehunter.R.layout.activity_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            // Enable Home Button
            setHomeButtonEnabled(true)
            // Show HOme Button
            setDisplayShowHomeEnabled(true)
            // Set Home Button to act as up navigation
            setDisplayHomeAsUpEnabled(true)
        }

        packageHunter = PackageHunter(this)

        pkgNameOfApp = intent.getStringExtra("data")

        val appName = packageHunter.getAppNameForPkg(pkgNameOfApp)

        txtvw_vname.text = "Version : ${packageHunter.getVersionForPkg(pkgNameOfApp)}"
        txtvw_vc.text = "Version Code : ${packageHunter.getVersionCodeForPkg(pkgNameOfApp)}"
        txtvw_firsttime.text = "First Install Time : ${getFormattedUpTime(packageHunter.getFirstInstallTimeForPkg(pkgNameOfApp))}"
        txtvw_lastupdated.text = "Last Update Time : ${getFormattedUpTime(packageHunter.getLastUpdatedTimeForPkg(pkgNameOfApp))}"

        imgvw_icn.setImageDrawable(packageHunter.getIconForPkg(pkgNameOfApp))

        txtvw_pkgname.text = appName

        supportActionBar?.title = appName

        val adapter = RVDetailsAdapter(getInfoLists(pkgNameOfApp))
        rv_detaillist.setHasFixedSize(true)
        rv_detaillist.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rv_detaillist.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getInfoLists(packageName: String): ArrayList<ElementInfo> {

        val permissions = packageHunter.getPermissionForPkg(packageName)
        val activities = packageHunter.getActivitiesForPkg(packageName)
        val services = packageHunter.getServicesForPkg(packageName)
        val providers = packageHunter.getProvidersForPkg(packageName)
        val receivers = packageHunter.getReceiverForPkg(packageName)


        val elementInfoArrayList = ArrayList<ElementInfo>()
        elementInfoArrayList.add(ElementInfo("Permissions", permissions))
        elementInfoArrayList.add(ElementInfo("Services", services))
        elementInfoArrayList.add(ElementInfo("Activities", activities))
        elementInfoArrayList.add(ElementInfo("Providers", providers))
        elementInfoArrayList.add(ElementInfo("Receivers", receivers))

        return elementInfoArrayList
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getFormattedUpTime(millis: Long): String {
        val sec = (millis / 1000).toInt() % 60
        val min = (millis / (1000 * 60) % 60).toInt()
        val hr = (millis / (1000 * 60 * 60) % 24).toInt()

        return String.format(Locale.US, "%02d:%02d:%02d", hr, min, sec)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_remove -> {
                packageHunter.uninstallPackage(pkgNameOfApp)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
