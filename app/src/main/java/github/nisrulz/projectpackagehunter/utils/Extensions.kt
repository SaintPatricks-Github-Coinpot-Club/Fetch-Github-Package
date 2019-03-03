package github.nisrulz.projectpackagehunter.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openInBrowser(urlString: String) {
    val uri = Uri.parse(urlString)
    val browserIntent = Intent(Intent.ACTION_VIEW, uri)
    this.startActivity(browserIntent)
}
