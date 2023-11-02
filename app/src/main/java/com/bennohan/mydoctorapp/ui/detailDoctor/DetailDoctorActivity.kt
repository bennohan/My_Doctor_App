package com.bennohan.mydoctorapp.ui.detailDoctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.databinding.ActivityDetailDoctorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDoctorActivity :
    BaseActivity<ActivityDetailDoctorBinding, DetailDoctorViewModel>(R.layout.activity_detail_doctor) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}