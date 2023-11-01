package com.bennohan.mydoctorapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.base.BaseFragment
import com.bennohan.mydoctorapp.data.Doctor
import com.bennohan.mydoctorapp.databinding.FragmentHomeBinding
import com.bennohan.mydoctorapp.databinding.ItemDoctorBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel by activityViewModels<HomeViewModel>()

    private val adapterDoctor by lazy {
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

//                    holder.binding.cardProduct.setOnClickListener {
//                        openActivity<DetailProductActivity> {
//                            intent.putExtra(Const.PRODUCT.PRODUCT_ID, item.id)
//                        }
//
//                    }


                }

            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.rvDoctorSebelumnya?.adapter = adapterDoctor

        getDoctor()
        observe()

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
//                    launch {
//                        //TODO The different between collect and collect latest
//                        viewModel.listNote.collectLatest { listNote ->
//                            adapterNote.submitList(listNote)
//                            dataNote.clear()
//                            dataNote.addAll(listNote)
//                        }
//                    }
                }
            }
        }



    private fun getDoctor() {
        viewModel.getDoctor()
    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_home, container, false)
//    }

}