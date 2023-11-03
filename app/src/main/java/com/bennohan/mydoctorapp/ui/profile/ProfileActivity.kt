package com.bennohan.mydoctorapp.ui.profile

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.UserDao
import com.bennohan.mydoctorapp.databinding.ActivityProfileBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.snacked
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        observe()

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            //TODO Loading dialog at fragment
                            ApiStatus.LOADING -> {}
                            ApiStatus.SUCCESS -> {
                                //TODO Replace it with TOAST
                                binding.root.snacked("Profile Edited")
                            }
                            ApiStatus.ERROR -> {
//                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> binding.root.snacked("error")
                        }

                    }
                }
                launch {
                    userDao.getUser().collectLatest { user ->
                        binding.user = user

                    }
                }
            }
        }

    }
}