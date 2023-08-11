package com.yifeplayte.applocalepicker.activity.pages

import android.annotation.SuppressLint
import android.app.LocaleConfig
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.provider.Settings
import cn.fkj233.ui.activity.annotation.BMMainPage
import cn.fkj233.ui.activity.data.BasePage
import cn.fkj233.ui.activity.fragment.MIUIFragment
import cn.fkj233.ui.activity.view.TextSummaryV
import cn.fkj233.ui.activity.view.TextSummaryWithArrowV
import com.yifeplayte.applocalepicker.R
import io.github.ranlee1.jpinyin.PinyinFormat
import io.github.ranlee1.jpinyin.PinyinHelper

@SuppressLint("NonConstantResourceId")
@BMMainPage(titleId = R.string.app_name)
class MainPage : BasePage() {
    init {
        skipLoadItem = true
    }

    override fun asyncInit(fragment: MIUIFragment) {
        fragment.showLoading()
        try {
            @Suppress("DEPRECATION") val applicationsInfo =
                activity.packageManager.getInstalledApplications(0).filter { it.isSupportLocalePicker() }.associateWith {
                    val label = it.loadLabel(activity.packageManager).toString()
                    PinyinHelper.convertToPinyinString(label, "", PinyinFormat.WITHOUT_TONE).lowercase()
                }.entries.sortedBy { it.value }.map { it.key }
            for (i in applicationsInfo) {
                fragment.addItem(
                    TextSummaryWithArrowV(
                        TextSummaryV(
                            text = i.loadLabel(activity.packageManager).toString(),
                            tips = i.packageName
                        ) {
                            val intent = Intent().apply {
                                action = Settings.ACTION_APP_LOCALE_SETTINGS
                                data = Uri.parse("package:" + i.packageName)
                            }
                            activity.startActivity(intent)
                        }
                    )
                )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        fragment.closeLoading()
        fragment.initData()
    }

    fun ApplicationInfo.isSupportLocalePicker(): Boolean {
        if ((flags and ApplicationInfo.FLAG_SYSTEM) == 1) return false
        val localeConfig = LocaleConfig(activity.createPackageContext(packageName, 0))
        val localeList = localeConfig.supportedLocales.takeIf { localeConfig.status == LocaleConfig.STATUS_SUCCESS } ?: return false
        return localeList.size() > 0
    }

    override fun onCreate() {}
}