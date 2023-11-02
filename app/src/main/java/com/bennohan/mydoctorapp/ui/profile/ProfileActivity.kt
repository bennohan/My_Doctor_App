package com.bennohan.mydoctorapp.ui.profile

import android.os.Bundle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding , ProfileViewModel>(R.layout.activity_profile) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}