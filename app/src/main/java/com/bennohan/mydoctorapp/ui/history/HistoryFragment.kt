package com.bennohan.mydoctorapp.ui.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseFragment
import com.bennohan.mydoctorapp.data.UserDao
import com.bennohan.mydoctorapp.databinding.FragmentHistoryBinding
import com.bennohan.mydoctorapp.databinding.FragmentHomeBinding
import com.bennohan.mydoctorapp.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history) {

    @Inject
    lateinit var userDao: UserDao
    private val viewModel by activityViewModels<HistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }


}