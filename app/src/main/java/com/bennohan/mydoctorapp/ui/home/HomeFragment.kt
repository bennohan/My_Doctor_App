package com.bennohan.mydoctorapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseFragment
import com.bennohan.mydoctorapp.data.Const
import com.bennohan.mydoctorapp.data.bannerSlider.BannerSlider
import com.bennohan.mydoctorapp.data.category.Category
import com.bennohan.mydoctorapp.data.doctor.Doctor
import com.bennohan.mydoctorapp.data.subdistrict.Subdistrict
import com.bennohan.mydoctorapp.data.user.UserDao
import com.bennohan.mydoctorapp.databinding.FragmentHomeBinding
import com.bennohan.mydoctorapp.databinding.ItemDoctorBinding
import com.bennohan.mydoctorapp.databinding.ItemDoctorCategoryBinding
import com.bennohan.mydoctorapp.databinding.ItemKecamatanBinding
import com.bennohan.mydoctorapp.ui.detailDoctor.DetailDoctorActivity
import com.bennohan.mydoctorapp.ui.profile.ProfileActivity
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.base.adapter.ReactiveListAdapter
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    private var dataSubdistrict = ArrayList<Subdistrict?>()
    private var dataCategory = ArrayList<Category?>()
    private var subdistrictsId: String? = null
    private var categoryId: String? = null
    private val imageBannerList = ArrayList<BannerSlider>()


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

    private val adapterKecamatan by lazy {
        object : ReactiveListAdapter<ItemKecamatanBinding, Subdistrict>(R.layout.item_kecamatan) {
            private var selectedPosition: Int = RecyclerView.NO_POSITION
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemKecamatanBinding, Subdistrict>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)

                item?.let { itm ->
                    holder.binding.kecamatan = itm
                    holder.bind(itm)

                    val isSelected = position == selectedPosition
                    if (isSelected) {
                        // Do something when the item is selected
                        holder.binding.cardSubsdistrict.setBackgroundColor(
                            requireContext().getColor(
                                R.color.main_color_red
                            )
                        )
                    } else {
                        // Do something when the item is not selected
                        holder.binding.cardSubsdistrict.setBackgroundColor(
                            requireContext().getColor(
                                R.color.white
                            )
                        )
                    }


                    holder.binding.cardSubsdistrict.setOnClickListener {
                        // Select the clicked item
                        notifyItemChanged(selectedPosition)

                        // Select the clicked item
                        selectedPosition = holder.adapterPosition
                        notifyItemChanged(position)

                        itm.selected = !itm.selected

                        // Notify the adapter that the data set has changed
                        notifyDataSetChanged()
                        Log.d("cek selected Kecamatan", itm.name)
                        subdistrictsId = itm.id
                        subdistrictsId?.let { it1 -> Log.d("cek selected KecamatanID", it1) }

                    }

                }

            }

        }
    }
    private val adapterCategoryDoctor by lazy {
        object :
            ReactiveListAdapter<ItemDoctorCategoryBinding, Category>(R.layout.item_doctor_category) {
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemDoctorCategoryBinding, Category>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)



                item?.let { itm ->
                    holder.binding.category = itm
                    holder.bind(itm)

                    if (itm.selected) {
                        // Do something when the item is selected
                        holder.binding.cardCategory.setBackgroundColor(requireContext().getColor(R.color.main_color_red))
                    } else {
                        // Do something when the item is not selected
                        holder.binding.cardCategory.setBackgroundColor(requireContext().getColor(R.color.white))
                    }


                    holder.binding.cardCategory.setOnClickListener {
                        itm.selected = !itm.selected

                        // Notify the adapter that the data set has changed
                        notifyDataSetChanged()
                        Log.d("cek selected Kecamatan", itm.name)
                        categoryId = itm.id

                    }


                }

            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.rvDoctorSebelumnya?.adapter = adapterDoctor

        binding?.btnFilter?.setOnClickListener {
            showBottomSheetDialog()
        }

        binding?.ivProfile?.setOnClickListener {
            val intent = Intent(requireContext(), ProfileActivity::class.java)
            startActivity(intent)

        }

        getDoctor()
        observe()
        search()
        viewModel.getBannerSlider()

    }

    private fun imageSlider(data: List<BannerSlider>) {
        val imageList = ArrayList<SlideModel>()
        data.forEach {
            imageList.add(SlideModel(it.photo))
        }
//        val ivSlider = find
        binding?.ivSliderBanner?.setImageList(imageList)
        Log.d("cek image slider", "$imageList")

    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {}
                            ApiStatus.SUCCESS -> {
                                when (it.message) {
                                    "data success" -> {
                                        viewModel.getCategories()
                                        viewModel.getSubdistricts()
                                    }
                                }
                            }
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
                launch {
                    viewModel.listSubdistrict.collectLatest {
                        adapterKecamatan.submitList(it)
                        dataSubdistrict.clear()
                        dataSubdistrict.addAll(it)

                    }
                }
                launch {
                    viewModel.listCategory.collectLatest {
                        adapterCategoryDoctor.submitList(it)
                        dataCategory.clear()
                        dataCategory.addAll(it)
                    }
                }
                launch {
                    viewModel.listBannerSlider.collectLatest {
                        Log.d("cek image collect", it.toString())
                        imageBannerList.clear()
                        imageBannerList.addAll(it)
                        imageSlider(it)
                    }
                }
                launch {
                    viewModel.listDoctorFilter.collectLatest {
                        adapterDoctor.submitList(it)
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

    private fun showBottomSheetDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_filter, null)
        val buttonInsideDialog = view.findViewById<Button>(R.id.btn_dialog_filter)
        val rvFilterKecamatan = view.findViewById<RecyclerView>(R.id.rv_kecamatan)
        val rvFilterCategory = view.findViewById<RecyclerView>(R.id.rv_doctorCategory)
        rvFilterKecamatan.adapter = adapterKecamatan
        rvFilterCategory.adapter = adapterCategoryDoctor

        // Find and set up UI components inside the bottom sheet layout

        buttonInsideDialog.setOnClickListener {
            subdistrictsId?.let { it1 -> viewModel.getDoctorFilter(it1,categoryId) }
            //Get List Destination By Category
            // Handle button click inside the bottom sheet dialog
            bottomSheetDialog.dismiss() // Close the dialog if needed

        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }


}