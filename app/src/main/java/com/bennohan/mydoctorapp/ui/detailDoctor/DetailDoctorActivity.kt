package com.bennohan.mydoctorapp.ui.detailDoctor

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.Const
import com.bennohan.mydoctorapp.data.doctor.Doctor
import com.bennohan.mydoctorapp.databinding.ActivityDetailDoctorBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.crocodic.core.helper.ImagePreviewHelper
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint

class DetailDoctorActivity :
    BaseActivity<ActivityDetailDoctorBinding, DetailDoctorViewModel>(R.layout.activity_detail_doctor) {

    private var doctorSave: Boolean? = null
    private var dataDoctor: Doctor? = null
    private var selectedHour: String? = null
    private var selectedHourBoolean: Boolean? = false
    private var dataDoctorImage: List<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getDoctor()
        observe()
        dataDoctorImage?.let { initSlider(it) }


        binding.ivProfileDoctor.setOnClickListener {
            ImagePreviewHelper(this).show(binding.ivProfileDoctor, binding.data?.photo)
        }

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

    private fun initSlider(data: List<String>) {
        val imageList = ArrayList<SlideModel>()
        data.forEach {
            imageList.add(SlideModel(it))
        }
//        binding.ivProfileDoctor.setImageList(imageList)
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
        val etAlasan = dialog.findViewById<EditText>(R.id.et_alasanKeluhan2)
        val btnBuatJanjiTemu = dialog.findViewById<Button>(R.id.btn_buatJanji)
        val tvDoctorName = dialog.findViewById<TextView>(R.id.tv_name)
        val ivDoctorPhoto = dialog.findViewById<ImageView>(R.id.iv_profile)
        val tvEditTime = dialog.findViewById<TextView>(R.id.tvEdit_waktuJanjiTemu)

        val tvDateTime = dialog.findViewById<TextView>(R.id.et_waktuJanjiTemuJam)

        val spannableString = SpannableString("Pilih jam di sini")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                showTimePicker(tvDateTime)
            }
        }

        spannableString.setSpan(
            clickableSpan,
            0,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvDateTime.text = spannableString
        tvDateTime.movementMethod =
            LinkMovementMethod.getInstance()
        // Required for clickable spans to work


        tvDoctorName.text = dataDoctor?.name

        Glide
            .with(this)
            .load(dataDoctor?.photo)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(ivDoctorPhoto)

        if (selectedHourBoolean == true) {
            tvEditTime.visibility = View.VISIBLE
        } else {
            tvEditTime.visibility = View.GONE
        }

        btnBuatJanjiTemu.setOnClickListener {
            val alasanKeluhan = etAlasan.text.toString()
            val dateTime = tvDateTime.textOf()

            if (selectedHour.isNullOrBlank() || alasanKeluhan.isNullOrBlank()) {
                tos("Harap Isi Semua Data")
                Log.d("cek ga", "$selectedHour")
                Log.d("cek ga2", "$alasanKeluhan")
                return@setOnClickListener
            } else {
                viewModel.createReservations(idDoctor.toString(), dateTime, alasanKeluhan)
                dialog.dismiss()
//                tos(" Semua Data Komplit")
                Log.d("cek ga", "$selectedHour")
                Log.d("cek ga2", "$alasanKeluhan")
            }

        }
//        dialog.setCancelable(false)
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
            { _, hourOfDay, minute ->
                if (hourOfDay in 9..17) {
                    val time = String.format("%02d:%02d", hourOfDay, minute)
                    val currentDate = dateFormat.format(calendar.time)
                    textView.text = "$currentDate $time"
                    selectedHour = "$currentDate $time"
                    selectedHourBoolean = true
                    Log.d("cek boolean",selectedHourBoolean.toString())
                } else {
                    Toast.makeText(
                        this,
                        R.string.note_reservation_failed,
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            currentHour,
            currentMinute,
            false

        )
        timePickerDialog.show()
    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {
                                loadingDialog.show()
                            }
                            ApiStatus.SUCCESS -> {
                                when (it.message) {
                                    "Reservation Created" -> {
                                        loadingDialog.dismiss()
                                        loadingDialog.setMessage("Reservasi Sukses")
                                        tos("Reservasi sukses")
                                    }
                                    "Saved" -> {
                                        if (doctorSave == true) {
                                            loadingDialog.dismiss()
                                            binding.root.snacked("Doctor UnSaved")
                                            getDoctor()
                                        } else {
                                            loadingDialog.dismiss()
                                            binding.root.snacked("Doctor Saved")
                                            getDoctor()
                                        }
                                    }
                                }
                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)

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
                launch {
                    viewModel.doctorImageList.collectLatest {
                        Log.d("cek image", it.toString())
                    }
                }
            }
        }
    }

}