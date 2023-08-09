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
import java.util.Locale

@SuppressLint("NonConstantResourceId")
@BMMainPage(titleId = R.string.app_name)
class MainPage : BasePage() {
    init {
        skipLoadItem = true
    }

    override fun asyncInit(fragment: MIUIFragment) {
        fragment.showLoading()
        try {
            @Suppress("DEPRECATION") val applicationsInfo = activity.packageManager.getInstalledApplications(0)
            applicationsInfo.sortWith { u1, u2 ->
                return@sortWith PinyinHelper.convertToPinyinString(
                    u1.loadLabel(activity.packageManager).toString(),
                    "",
                    PinyinFormat.WITHOUT_TONE
                ).lowercase(Locale.ROOT).compareTo(
                    PinyinHelper.convertToPinyinString(
                        u2.loadLabel(activity.packageManager).toString(),
                        "",
                        PinyinFormat.WITHOUT_TONE
                    ).lowercase(Locale.ROOT)
                )
            }
            for (i in applicationsInfo) {
                if ((i.flags and ApplicationInfo.FLAG_SYSTEM) != 1) {
                    val localeConfig = LocaleConfig(activity.createPackageContext(i.packageName, 0))
                    val localeList = localeConfig.supportedLocales.takeIf { localeConfig.status == LocaleConfig.STATUS_SUCCESS }
                    if (localeList != null && localeList.size() > 0)
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
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        fragment.closeLoading()
        fragment.initData()
    }

    override fun onCreate() {}
}