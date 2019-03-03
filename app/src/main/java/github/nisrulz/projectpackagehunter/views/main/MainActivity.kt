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

package github.nisrulz.projectpackagehunter.views.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import github.nisrulz.packagehunter.PackageHunter
import github.nisrulz.packagehunter.PkgInfo
import github.nisrulz.projectpackagehunter.R
import github.nisrulz.projectpackagehunter.R.string
import github.nisrulz.projectpackagehunter.utils.openInBrowser
import github.nisrulz.projectpackagehunter.views.AboutActivity
import github.nisrulz.projectpackagehunter.views.detail.DetailActivity
import github.nisrulz.recyclerviewhelper.RVHItemClickListener
import github.nisrulz.recyclerviewhelper.RVHItemClickListener.OnItemClickListener
import kotlinx.android.synthetic.main.toolbar.toolbar
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var adapter: RVMainAdapter

    private lateinit var packageHunter: PackageHunter

    private lateinit var pkgInfoArrayList: ArrayList<PkgInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        packageHunter = PackageHunter(this)

        val rv = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_pkglist)
        pkgInfoArrayList = packageHunter.installedPackages

        adapter = RVMainAdapter(this, pkgInfoArrayList)
        rv.hasFixedSize()
        rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        rv.adapter = adapter

        rv.visibility = View.VISIBLE

        // Set On Click
        rv.addOnItemTouchListener(
                RVHItemClickListener(this, OnItemClickListener { view, position ->
                    val i = Intent(this@MainActivity, DetailActivity::class.java)
                    i.putExtra("data", pkgInfoArrayList[position].packageName)
                    startActivity(i)
                }))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        val searchViewItem = menu.findItem(R.id.action_search)
        val searchViewAndroidActionBar = searchViewItem.actionView as SearchView
        searchViewAndroidActionBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String): Boolean {

                pkgInfoArrayList = packageHunter.searchInList(query, PackageHunter.PACKAGES)
                adapter.updateWithNewListData(pkgInfoArrayList)

                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                searchViewAndroidActionBar.clearFocus()
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                pkgInfoArrayList = packageHunter.installedPackages
                adapter.updateWithNewListData(pkgInfoArrayList)
            }
            R.id.action_about -> {
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            R.id.action_privacy -> {
                openInBrowser(getString(string.url_privacy_policy))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
