package com.bennohan.mydoctorapp.ui.history

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseFragment
import com.bennohan.mydoctorapp.data.historyDoctor.HistoryReservation
import com.bennohan.mydoctorapp.data.user.UserDao
import com.bennohan.mydoctorapp.databinding.FragmentHistoryBinding
import com.bennohan.mydoctorapp.databinding.ItemHistoryDoctorBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    private var dataHistory: HistoryReservation? = null

    private val adapterHistoryOrder by lazy {
        object :
            ReactiveListAdapter<ItemHistoryDoctorBinding, HistoryReservation>(R.layout.item_history_doctor) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemHistoryDoctorBinding, HistoryReservation>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)


                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)


                    when (item.status) {
                        "done" -> {
                            holder.binding.tvStatusReservation.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.my_hint_color
                                )
                            )
                        }
                        "hold" -> {
                            holder.binding.tvStatusReservation.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.grey
                                )
                            )
                        }

                    }


                    holder.binding.cardDoctor.setOnClickListener {
                        dataHistory = itm
                        Log.d("cek data history single", dataHistory.toString())
                        openDialogHistory()

                    }


                }

            }

        }
    }

    private fun openDialogHistory() {
        val dialog = Dialog(requireContext())
        val dataDialog = dataHistory
        dialog.setContentView(R.layout.dialog_history)

        Log.d("cek data history 1", dataDialog.toString())

        val tvName = dialog.findViewById<TextView>(R.id.tv_name)
        val tvDoctorCategory = dialog.findViewById<TextView>(R.id.tv_doctorCategory)
        val tvTimeReservation = dialog.findViewById<TextView>(R.id.tv_timeReservation)
        val tvRemarks = dialog.findViewById<TextView>(R.id.tv_remarks)
        val tvStatus = dialog.findViewById<TextView>(R.id.tv_status)
        val ivProfile = dialog.findViewById<ImageView>(R.id.iv_profile)

        tvName.text = dataDialog?.docter?.name
        tvDoctorCategory.text = dataDialog?.docter?.category?.name
        tvTimeReservation.text = dataDialog?.timeReservation
        tvRemarks.text = dataDialog?.remarks

        if (dataDialog != null) {
            when (dataDialog.status) {
                "done" -> {
                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.my_hint_color
                        )
                    )
                }
                "hold" -> {
                    tvStatus.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
                }

            }
        }

        tvStatus.text = dataDialog?.status

        Glide
            .with(requireContext())
            .load(dataDialog?.docter?.photo)
            .apply(RequestOptions.centerCropTransform())
            .placeholder(R.drawable.ic_baseline_person_24)
            .into(ivProfile)

        dialog.show()
    }


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
                        Log.d("cek history", listHistory.toString())
                        if (listHistory.isEmpty()) {
                            binding?.tvAdapterEmpty?.visibility = View.VISIBLE
                        } else {
                            binding?.tvAdapterEmpty?.visibility = View.GONE
                        }

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