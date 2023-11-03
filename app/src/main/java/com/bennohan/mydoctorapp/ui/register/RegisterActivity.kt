package com.bennohan.mydoctorapp.ui.register

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.Subdistrict
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

    private var dataSubdistrict = ArrayList<Subdistrict?>()
    private var subdistrictsId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observe()
        validateRegister()
        viewModel.getSubdistricts()
        autocompleteSpinner()




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
        val email = binding.etEmail.textOf()
        val password = binding.etPassword.textOf()
        val confirmPassword = binding.etConfirmPassword.textOf()

        if (binding.etName.isEmptyRequired(R.string.mustFillName) || binding.etPhone.isEmptyRequired(
                R.string.mustFillPhone
            ) || binding.textInputKecamatan.isEmptyRequired(R.string.mustFillKecamatan) || binding.etEmail.isEmptyRequired(
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
//            tos("condition 2")
        } else {
            // If Password Doesn't Valid
            binding.tvPasswordMinLength.visibility = View.GONE
            binding.tvPasswordNotMatch.visibility = View.VISIBLE
            tos("condition 3")
            return
        }

        subdistrictsId?.let {
            viewModel.register(name, email, phone,
                it, password, confirmPassword)
        }
        Log.d("Cek Kirim","$name,$phone$subdistrictsId,$email,$password,$confirmPassword")
    }

    private fun autocompleteSpinner() {

        val autoCompleteSpinner = findViewById<AutoCompleteTextView>(R.id.textInputKecamatan)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dataSubdistrict)
        autoCompleteSpinner.setAdapter(adapter)
        binding.textInputKecamatan.setTextColor(ContextCompat.getColor(this,R.color.black))


        // Show the dropdown list when the AutoCompleteTextView is clicked
        autoCompleteSpinner.setOnClickListener {
            autoCompleteSpinner.showDropDown()
            autoCompleteSpinner.dropDownVerticalOffset = -autoCompleteSpinner.height

        }

        autoCompleteSpinner.setOnItemClickListener { _, _, position, _ ->
            // Handle item selection here
            val selectedItem = dataSubdistrict[position]
            subdistrictsId = selectedItem?.id


        }

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {

                            }
                            ApiStatus.ERROR -> {
                                disconnect(it)
                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> loadingDialog.setResponse(it.message ?: return@collect)
                        }

                    }
                }
                launch {
                    viewModel.listSubdistrict.collect {
                        dataSubdistrict.addAll(it)
//                        val items = apiResponse.items.toList()
                        // Call a function to set the spinner items with the retrieved data
                    }
                }
            }
        }
    }


}