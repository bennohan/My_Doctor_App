package com.bennohan.mydoctorapp.ui.forgotPassword

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.databinding.ActivityPasswordBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordActivity :
    BaseActivity<ActivityPasswordBinding, PasswordViewModel>(R.layout.activity_password) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.btnResetPassword.setOnClickListener {
            resetPassword()
        }
        observe()

    }


    private fun resetPassword() {
        val oldPassword = binding.etOldPassword.textOf()
        val newPassword = binding.etNewPassword.textOf()
        val confirmPassword = binding.etConfirmPassword.textOf()

        if (binding.etOldPassword.isEmptyRequired(R.string.mustFillOldPassword) || binding.etNewPassword.isEmptyRequired(
                R.string.mustFillPassword
            ) || binding.etConfirmPassword.isEmptyRequired(R.string.mustFillConfirmPassword)
        ) {
            return
        }

        if (binding.etNewPassword.textOf() == binding.etConfirmPassword.textOf()) {
            // If Password is valid
            binding.tvPasswordNotMatch.visibility = View.GONE
            binding.tvPasswordNotMatch.visibility = View.GONE
//            tos("condition 2")
        } else {
            // If Password Doesn't Valid
            binding.tvPasswordMinLength.visibility = View.GONE
            binding.tvPasswordNotMatch.visibility = View.VISIBLE
            tos("condition 3")
            return
        }


        viewModel.resetPassword(oldPassword, newPassword, confirmPassword)

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                finish()
                                loadingDialog.show("Reset Password Success")
                                tos("Reset Password Success")

                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }

                    }
                }
            }
        }
    }


}