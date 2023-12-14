package com.bennohan.mydoctorapp.ui.login

import android.content.ContentValues
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.Const
import com.bennohan.mydoctorapp.databinding.ActivityLoginBinding
import com.bennohan.mydoctorapp.ui.forgotPassword.PasswordActivity
import com.bennohan.mydoctorapp.ui.home.NavigationActivity
import com.bennohan.mydoctorapp.ui.register.RegisterActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.data.CoreSession
import com.crocodic.core.extension.isEmptyRequired
import com.crocodic.core.extension.openActivity
import com.crocodic.core.extension.textOf
import com.crocodic.core.extension.tos
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>(R.layout.activity_login) {

    @Inject
    lateinit var session: CoreSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Calling Function
        observe()
        tvRegisterOption()
        generateFcmToken { }


        binding.btnLogin.setOnClickListener {
            login()
        }

        binding.tvForgetPassword.setOnClickListener {
            openActivity<PasswordActivity>()
        }


    }

    private fun tvRegisterOption() {
        val spannableString = SpannableString("Don’t have an account? Register")
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                openActivity<RegisterActivity>()
            }
        }
        spannableString.setSpan(
            clickableSpan,
            23,
            spannableString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvOptionRegister.text = spannableString
        binding.tvOptionRegister.movementMethod =
            LinkMovementMethod.getInstance() // Required for clickable spans to work

    }

    //Login Function
    private fun login() {
        val emailPhone = binding.etEmailPhone.textOf()
        val password = binding.etPassword.textOf()

        if (binding.etEmailPhone.isEmptyRequired(R.string.mustFillPhoneEmail) || binding.etPassword.isEmptyRequired(
                R.string.mustFillPassword
            )
        ) {
            return
        }
        viewModel.login(emailPhone, password)

    }

    private fun generateFcmToken(result: (Boolean) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
//                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                result(false)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            //Receive FCM Task result
            val token = task.result

            // Log and toast
            Log.d(ContentValues.TAG, token)
//            session.setValue(Const.TOKEN.DEVICE_TOKEN, token)
            session.setValue(Const.TOKEN.DEVICE_TOKEN, token)
            result(true)
        })
    }

    //Observing Api Response Function
    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> loadingDialog.show()
                            ApiStatus.SUCCESS -> {
                                openActivity<NavigationActivity> {
                                    finish()
                                    loadingDialog.show("Login Success")
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