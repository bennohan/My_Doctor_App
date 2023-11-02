package com.bennohan.mydoctorapp.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseFragment
import com.bennohan.mydoctorapp.data.Doctor
import com.bennohan.mydoctorapp.data.UserDao
import com.bennohan.mydoctorapp.databinding.FragmentHomeBinding
import com.bennohan.mydoctorapp.databinding.ItemDoctorBinding
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    @Inject
    lateinit var userDao: UserDao
    private val viewModel by activityViewModels<HomeViewModel>()
    private var dataDoctor = ArrayList<Doctor?>()


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
        search()

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
                    viewModel.listDoctor.collectLatest { listDoctor ->
                        adapterDoctor.submitList(listDoctor)
                        dataDoctor.clear()
                        dataDoctor.addAll(listDoctor)
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

    private fun search() {
        binding?.etSearch?.doOnTextChanged { text, _, _, _ ->
            if (text!!.isNotEmpty()) {
                val filter = dataDoctor.filter {
                    it?.name?.contains(
                        "$text",
                        true
                    ) == true
                }
                adapterDoctor.submitList(filter)

//                if (filter.isEmpty()) {
//                    binding!!.tvNoteNotFound.visibility = View.VISIBLE
//                } else {
//                    binding!!.tvNoteNotFound.visibility = View.GONE
//                }
            } else {
                adapterDoctor.submitList(dataDoctor)
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