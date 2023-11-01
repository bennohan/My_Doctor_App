package com.bennohan.mydoctorapp.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.UserDao
import com.bennohan.mydoctorapp.databinding.ActivityMainBinding
import com.bennohan.mydoctorapp.ui.home.NavigationActivity
import com.bennohan.mydoctorapp.ui.login.LoginActivity
import com.bennohan.mydoctorapp.ui.register.RegisterActivity
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                    openActivity<NavigationActivity> {
                        finish()
                    }
                }
            }

        }, 4000)

    }

}
