package com.yifeplayte.applocalepicker.activity

import cn.fkj233.ui.activity.MIUIActivity
import com.yifeplayte.applocalepicker.activity.pages.MainPage

class MainActivity : MIUIActivity() {
    init {
        activity = this
        registerPage(MainPage::class.java)
    }
}