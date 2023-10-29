package com.bennohan.mydoctorapp.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding , HomeViewModel>(R.layout.activity_home) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}