package com.bennohan.mydoctorapp.ui.save

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
import com.bennohan.mydoctorapp.data.doctor.Doctor
import com.bennohan.mydoctorapp.data.user.UserDao
import com.bennohan.mydoctorapp.databinding.FragmentSaveBinding
import com.bennohan.mydoctorapp.databinding.ItemDoctorBinding
import com.bennohan.mydoctorapp.ui.detailDoctor.DetailDoctorActivity
import com.bennohan.mydoctorapp.ui.profile.ProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SaveFragment : BaseFragment<FragmentSaveBinding>(R.layout.fragment_save) {

    @Inject
    lateinit var userDao: UserDao
    private val viewModel by activityViewModels<SaveViewModel>()

    private val adapterDoctorSaved by lazy {
        object : ReactiveListAdapter<ItemDoctorBinding, Doctor>(R.layout.item_doctor) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemDoctorBinding, Doctor>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)

                item?.let { itm ->
                    holder.binding.data = itm
                    holder.bind(itm)

                    holder.binding.cardDoctor.setOnClickListener {
                        val intent = Intent(requireContext(), DetailDoctorActivity::class.java)
                        intent.putExtra(Const.DOCTOR.ID_DOCTOR, item.id)
                        startActivity(intent)
                        Log.d("cek id doctor", "${item.id}")

                    }


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
//        return inflater.inflate(R.layout.fragment_save, container, false)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.rvDoctorSaved?.adapter = adapterDoctorSaved
        getDoctor()
        observe()
        binding?.ivProfile?.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)
        }

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
                    viewModel.listDoctorSave.collectLatest { listDoctor ->
                        adapterDoctorSaved.submitList(listDoctor)
                        Log.d("cek isian", "$listDoctor")
                        if (listDoctor.isEmpty()) {
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

    private fun getDoctor() {
        viewModel.getDoctorSave()
    }

}