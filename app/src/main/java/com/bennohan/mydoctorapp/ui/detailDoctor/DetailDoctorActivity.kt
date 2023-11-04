package com.bennohan.mydoctorapp.ui.detailDoctor

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.Const
import com.bennohan.mydoctorapp.databinding.ActivityDetailDoctorBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.tos
import com.denzcoskun.imageslider.models.SlideModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailDoctorActivity :
    BaseActivity<ActivityDetailDoctorBinding, DetailDoctorViewModel>(R.layout.activity_detail_doctor) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getDoctor()
        observe()
//        imageSlider()

        binding.btnBack.setOnClickListener {
            finish()
        }

    }

//    private fun imageSlider() {
//        val imageList = ArrayList<SlideModel>() // Create image list
//
//// imageList.add(SlideModel("String Url" or R.drawable)
//// imageList.add(SlideModel("String Url" or R.drawable, "title") You can add title
//
//        imageList.add(SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years."))
//        imageList.add(SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct."))
//        imageList.add(SlideModel("https://bit.ly/3fLJf72", "And people do that."))
//
////        val imageSlider = findViewById<ImageSlider>(R.id.image_slider)
//        binding.ivProfileDoctor.setImageList(imageList)
//    }


    private fun getDoctor() {
        val data = intent.getStringExtra(Const.DOCTOR.ID_DOCTOR)
        if (data != null) {
            viewModel.getDoctor(idDoctor = data)
        }
        else{
            tos("null")
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {}
                            ApiStatus.SUCCESS -> {}
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
                    }
                }
            }
        }
    }

}