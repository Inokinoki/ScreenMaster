package cc.inoki.screenmaster.helper

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import cc.inoki.screenmaster.model.AppInfo

class AppHelper(private val context: Context) {

    fun getLaunchableApps(): List<AppInfo> {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val apps = packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .map { resolveInfo ->
                AppInfo(
                    packageName = resolveInfo.activityInfo.packageName,
                    appName = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager)
                )
            }
            .sortedBy { it.appName }

        return apps
    }

    fun launchApp(packageName: String, displayId: Int) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            it.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)

            if (displayId != 0) {
                val options = android.app.ActivityOptions.makeBasic()
                options.launchDisplayId = displayId
                context.startActivity(it, options.toBundle())
            } else {
                context.startActivity(it)
            }
        }
    }
}
