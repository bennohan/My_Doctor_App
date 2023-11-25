package com.bennohan.mydoctorapp.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.data.user.UserDao
import com.bennohan.mydoctorapp.databinding.ActivityMainBinding
import com.bennohan.mydoctorapp.ui.home.NavigationActivity
import com.bennohan.mydoctorapp.ui.login.LoginActivity
import com.bennohan.mydoctorapp.ui.register.RegisterActivity
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

//TODO TEST INI
@AndroidEntryPoint
class MainActivity : NoViewModelActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var userDao: UserDao


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                val user = userDao.isLogin()
                if (!user) {
                    binding.layoutSplashOption.visibility = View.VISIBLE
                    binding.layoutUiSplash.visibility = View.GONE
                    binding.btnLogin.setOnClickListener {
                        openActivity<LoginActivity> {
                            finish()
                        }
                    }
                    binding.btnRegister.setOnClickListener{
                        openActivity<RegisterActivity> {
                            finish()
                        }
                    }
                } else {
                    binding.layoutSplashOption.visibility = View.GONE
                    binding.layoutUiSplash.visibility = View.VISIBLE
                    openActivity<NavigationActivity> {
                        finish()
                    }
                }
            }

        }, 4000)

    }

}
