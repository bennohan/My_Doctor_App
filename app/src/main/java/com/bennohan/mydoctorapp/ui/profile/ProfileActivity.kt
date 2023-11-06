package com.bennohan.mydoctorapp.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.UserDao
import com.bennohan.mydoctorapp.databinding.ActivityProfileBinding
import com.bennohan.mydoctorapp.ui.login.LoginActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.openActivity
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
        binding.btnLogout.setOnClickListener {
            logoutDialog()
        }

    }

    private fun logoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_logout)

        val buttonLogout = dialog.findViewById<Button>(R.id.btn_dialog_logout)
        val buttonCancel = dialog.findViewById<Button>(R.id.btn_dialog_cancel)
//
        buttonLogout.setOnClickListener {
            viewModel.logout()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


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
                                when(it.message){
                                    "Profile Edited" -> {
                                        binding.root.snacked("Profile Edited")

                                    }
                                    "Logout" -> {
                                        openActivity<LoginActivity> {
                                            userDao.deleteAll()
                                            finishAffinity()
                                        }
                                    }
                                }
                                //TODO Replace it with TOAST
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