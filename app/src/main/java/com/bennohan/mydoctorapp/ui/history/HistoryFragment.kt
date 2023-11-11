package com.bennohan.mydoctorapp.ui.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseFragment
import com.bennohan.mydoctorapp.data.Const
import com.bennohan.mydoctorapp.data.Doctor
import com.bennohan.mydoctorapp.data.UserDao
import com.bennohan.mydoctorapp.data.historyDoctor.HistoryReservation
import com.bennohan.mydoctorapp.databinding.FragmentHistoryBinding
import com.bennohan.mydoctorapp.databinding.ItemDoctorBinding
import com.bennohan.mydoctorapp.databinding.ItemHistoryBinding
import com.bennohan.mydoctorapp.ui.detailDoctor.DetailDoctorActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history) {

    @Inject
    lateinit var userDao: UserDao
    private val viewModel by activityViewModels<HistoryViewModel>()

    private val adapterHistoryOrder by lazy {
        object : ReactiveListAdapter<ItemHistoryBinding, HistoryReservation>(R.layout.item_history) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemHistoryBinding, HistoryReservation>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)


                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)

//                    holder.binding.cardDoctor.setOnClickListener {
//                        val intent = Intent(requireContext(), DetailDoctorActivity::class.java)
//                        intent.putExtra(Const.DOCTOR.ID_DOCTOR, item.id)
//                        startActivity(intent)
                        Log.d("cek id doctor", "${item.id}")

//                    }


                }

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_history, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.rvDoctorSebelumnya?.adapter = adapterHistoryOrder
        observe()
        getHistoryOrder()

    }

    private fun getHistoryOrder() {
        viewModel.getHistoryOrder()
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
                    viewModel.listHistoryOrder.collectLatest { listHistory ->
                        adapterHistoryOrder.submitList(listHistory)
                        Log.d("cek history",listHistory.toString())
//                        dataDoctor.clear()
//                        dataDoctor.addAll(listDoctor)
                    }
                }
                launch {
                    userDao.getUser().collect {
                        binding?.user = it
                    }
                }
            }
        }

    }


}