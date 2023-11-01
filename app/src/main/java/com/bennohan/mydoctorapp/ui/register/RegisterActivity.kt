package com.bennohan.mydoctorapp.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.databinding.ActivityRegisterBinding
import com.bennohan.mydoctorapp.ui.home.HomeActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterActivity :
    BaseActivity<ActivityRegisterBinding, RegisterViewModel>(R.layout.activity_register) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        validateRegister()



        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            register()
        }

    }

    private fun validateRegister() {
        binding.etPassword.doAfterTextChanged {
            validatePassword()
        }

        binding.etConfirmPassword.doAfterTextChanged {
            validatePassword()
            if (binding.etPassword.textOf().isEmpty()) {
                binding.etPassword.error = "Password Tidak Boleh Kosong"
                binding.tvPasswordNotMatch.visibility = View.GONE
            }
        }
    }

    private fun isValidPasswordLength(password: String): Boolean {
        return password.length >= 6
    }

    private fun validatePassword() {
        if (!isValidPasswordLength(binding.etPassword.textOf())) {
            //If Password length is not 6 character
            binding.tvPasswordMinLength.visibility = View.VISIBLE
            return
        } else {
            binding.tvPasswordMinLength.visibility = View.GONE
        }
        if (binding.etPassword.textOf() != binding.etConfirmPassword.textOf()) {
            //If Password  is not match
            binding.tvPasswordNotMatch.visibility = View.VISIBLE
            return
        } else {
            binding.tvPasswordNotMatch.visibility = View.GONE
        }
    }

    private fun register() {
        val name = binding.etName.textOf()
        val phone = binding.etPhone.textOf()
        val kecamatan = binding.etKecamatan.textOf()
        val email = binding.etEmail.textOf()
        val password = binding.etPassword.textOf()
        val confirmPassword = binding.etConfirmPassword.textOf()

        if (binding.etName.isEmptyRequired(R.string.mustFillName) || binding.etPhone.isEmptyRequired(
                R.string.mustFillPhone
            ) || binding.etKecamatan.isEmptyRequired(R.string.mustFillKecamatan) || binding.etEmail.isEmptyRequired(
                R.string.mustFillEmail
            )
            || binding.etPassword.isEmptyRequired(R.string.mustFillPassword) || binding.etConfirmPassword.isEmptyRequired(
                R.string.mustFillConfirmPassword
            )
        ) {
            return
        }

        //TODO ADD Email Validation Function
        fun isValidIndonesianPhoneNumber(phoneNumber: String): Boolean {
            val regex = Regex("^\\+62\\d{9,15}$|^0\\d{9,11}$")
            return regex.matches(phoneNumber)
        }
        if (!isValidIndonesianPhoneNumber(phone)) {
            // if Telephone number is not valid
            tos("condition 1")
            binding.etPhone.error = "Nomor Telephone Tidak Valid"
            return
        }
        if (binding.etPassword.textOf() == binding.etConfirmPassword.textOf()) {
            // If Password is valid
            binding.tvPasswordNotMatch.visibility = View.GONE
            binding.tvPasswordNotMatch.visibility = View.GONE
            tos("condition 2")
        } else {
            // If Password Doesn't Valid
            binding.tvPasswordMinLength.visibility = View.GONE
            binding.tvPasswordNotMatch.visibility = View.VISIBLE
            tos("condition 3")
            return
        }

        viewModel.register(name, email, phone, password, confirmPassword)
        tos("$name,$phone$kecamatan,$email,$password,$confirmPassword")
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                openActivity<HomeActivity> {
                                    finish()
                                    tos("Login Success")
                                }
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