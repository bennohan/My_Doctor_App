package com.bennohan.mydoctorapp.ui.detailDoctor

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.Const
import com.bennohan.mydoctorapp.data.Doctor
import com.bennohan.mydoctorapp.databinding.ActivityDetailDoctorBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailDoctorActivity :
    BaseActivity<ActivityDetailDoctorBinding, DetailDoctorViewModel>(R.layout.activity_detail_doctor) {

    private var doctorSave: Boolean? = null
    private  var dataDoctor : Doctor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getDoctor()
        observe()
//        imageSlider()


        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnSave.setOnClickListener {
            saveDoctor()
        }
        binding.btnBuatJanjiTemu.setOnClickListener {
            buatJanjiTemuDialog()
        }
    }

    

    private fun getDoctor() {
        val idDoctor = intent.getStringExtra(Const.DOCTOR.ID_DOCTOR)
        if (idDoctor != null) {
            viewModel.getDoctor(idDoctor)
        } else {
            tos("null")
        }
    }

    private fun saveDoctor() {
        val idDoctor = intent.getStringExtra(Const.DOCTOR.ID_DOCTOR)
        if (idDoctor != null) {
            viewModel.saveDoctor(idDoctor)
        }
    }

                                                                private fun buatJanjiTemuDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_buat_janji_temu)

        val idDoctor = intent.getStringExtra(Const.DOCTOR.ID_DOCTOR)
        val etAlasanKeluhan = dialog.findViewById<EditText>(R.id.et_alasanKeluhan)
        val tvDateTime = dialog.findViewById<TextView>(R.id.et_waktuJanjiTemuJam)
        val btnBuatJanjiTemu = dialog.findViewById<Button>(R.id.btn_buatJanji)
        val tvDoctorName = dialog.findViewById<TextView>(R.id.tv_name)
        val ivDoctorPhoto = dialog.findViewById<ImageView>(R.id.iv_profile)

        tvDoctorName.text = dataDoctor?.name

        Glide
            .with(this)
            .load(dataDoctor?.photo)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(ivDoctorPhoto)

        val alasanKeluhan = etAlasanKeluhan.textOf()
        val dateTime = tvDateTime.textOf()
        tvDateTime.setOnClickListener {
            showTimePicker(tvDateTime)
        }

        btnBuatJanjiTemu.setOnClickListener {
            if (idDoctor != null) {
                viewModel.createReservations(idDoctor,alasanKeluhan,dateTime)
            }
        }

        dialog.show()


    }


    private fun showTimePicker(textView: TextView) {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val nextHour = (currentHour + 1) % 24
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentMinute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                val currentDate = dateFormat.format(calendar.time)
                textView.text = "$currentDate $time"
            },
            nextHour,
            currentMinute,
            true
        )

        timePickerDialog.show()
    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {}
                            ApiStatus.SUCCESS -> {
                                when (it.message) {
                                    "Saved" -> {
                                        if (doctorSave == true) {
                                            binding.root.snacked("Doctor UnSaved")
                                            getDoctor()
                                        } else {
                                            binding.root.snacked("Doctor Saved")
                                            getDoctor()
                                        }
                                    }
                                }
                            }
                            ApiStatus.ERROR -> {
//                                disconnect(it)
//                                load.setResponse(it.message ?: return@collect)

                            }
                            else -> {}
                        }

                    }
                }
                launch {
                    viewModel.detailDoctor.collectLatest { detailDoctor ->
                        binding.data = detailDoctor
                        dataDoctor = detailDoctor
                        doctorSave = detailDoctor?.saveByYou
                    }
                }
//                launch {
//                    val like = viewModel.doctorLike
//                    tos("$like")
//                }
            }
        }
    }

}