@file:Suppress("DEPRECATION")

package com.bennohan.mydoctorapp.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
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
import com.denzcoskun.imageslider.constants.ScaleTypes
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
    private var subdistrictsSelectd: Boolean? = false
    private var categoryId: String? = null
    private var categorySelected: Boolean? = false
    private val imageBannerList = ArrayList<BannerSlider>()
    private var loadingDialog: ProgressDialog? = null


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

                    }


                }

            }

        }
    }

    private val adapterKecamatan by lazy {
        object : ReactiveListAdapter<ItemKecamatanBinding, Subdistrict>(R.layout.item_kecamatan) {
            // Var to indicate that no item is currently selected
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

                    // It checks if the current item being bound in the RecyclerView same to the selectedPosition
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
                        if (holder.adapterPosition == selectedPosition) {
                            // Clicked on the already selected item, so deselect it
                            selectedPosition = RecyclerView.NO_POSITION
                            subdistrictsSelectd = false
                        } else {
                            // Deselect the previously selected item
                            notifyItemChanged(selectedPosition)
                            // Select the clicked item
                            selectedPosition = holder.adapterPosition
                            notifyItemChanged(selectedPosition)
                            subdistrictsSelectd = true
                        }

                        // Notify the adapter that the data set has changed
                        notifyDataSetChanged()
                        subdistrictsId = itm.id

                    }

                }

            }

        }
    }
    private val adapterCategoryDoctor by lazy {
        object :
            ReactiveListAdapter<ItemDoctorCategoryBinding, Category>(R.layout.item_doctor_category) {
            // Var to indicate that no item is currently selected
            private var selectedPosition: Int = RecyclerView.NO_POSITION
            override fun onBindViewHolder(
                holder: ItemViewHolder<ItemDoctorCategoryBinding, Category>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)
                val item = getItem(position)



                item?.let { itm ->
                    holder.binding.category = itm
                    holder.bind(itm)

                    // It checks if the current item being bound in the RecyclerView same to the selectedPosition
                    val isSelected = position == selectedPosition
                    if (isSelected) {
                        // Do something when the item is selected
                        holder.binding.cardCategory.setBackgroundColor(requireContext().getColor(R.color.main_color_red))
                    } else {
                        // Do something when the item is not selected
                        holder.binding.cardCategory.setBackgroundColor(requireContext().getColor(R.color.white))
                    }


                    holder.binding.cardCategory.setOnClickListener {
                        if (holder.adapterPosition == selectedPosition) {
                            // Clicked on the already selected item, so deselect it
                            selectedPosition = RecyclerView.NO_POSITION
                            categorySelected = false
                        } else {
                            // Deselect the previously selected item
                            notifyItemChanged(selectedPosition)
                            // Select the clicked item
                            selectedPosition = holder.adapterPosition
                            notifyItemChanged(selectedPosition)
                            categorySelected = true
                        }

                        // Notify the adapter that the data set has changed
                        notifyDataSetChanged()
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

    }

    private fun imageSlider(data: List<BannerSlider>) {
        val imageList = ArrayList<SlideModel>()
        data.forEach {
            imageList.add(SlideModel(it.photo))
        }
        binding?.ivSliderBanner?.setImageList(imageList, ScaleTypes.FIT)

    }


    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            ApiStatus.LOADING -> {
                                showLoadingDialog()
                            }
                            ApiStatus.SUCCESS -> {
                                loadingDialog?.dismiss()
                                when (it.message) {
                                    "data success" -> {
                                        viewModel.getBannerSlider()
                                    }
                                    "slider Success" -> {
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
                        imageBannerList.clear()
                        imageBannerList.addAll(it)
                        imageSlider(it)
                    }
                }
                launch {
                    viewModel.listDoctorFilter.collectLatest {
                        if (it.isEmpty()) {
                            binding?.tvDoctorNotFound?.visibility = View.VISIBLE
                        } else {
                            binding?.tvDoctorNotFound?.visibility = View.GONE
                        }
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

                if (filter.isEmpty()) {
                    binding!!.tvDoctorNotFound.visibility = View.VISIBLE
                } else {
                    binding!!.tvDoctorNotFound.visibility = View.GONE
                }

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
        val buttonFilterDialog = view.findViewById<Button>(R.id.btn_dialog_filter)
        val rvFilterKecamatan = view.findViewById<RecyclerView>(R.id.rv_kecamatan)
        val rvFilterCategory = view.findViewById<RecyclerView>(R.id.rv_doctorCategory)
        rvFilterKecamatan.adapter = adapterKecamatan
        rvFilterCategory.adapter = adapterCategoryDoctor

        // Find and set up UI components inside the bottom sheet layout

        buttonFilterDialog.setOnClickListener {
            // Handle button click inside the bottom sheet dialog
            when {
                subdistrictsSelectd == false && categorySelected == false -> {
                    // Both selectedId and categoryId are null
                    adapterDoctor.submitList(dataDoctor)
                }
                subdistrictsSelectd == false -> {
                    // Only selectedId is null
                    subdistrictsId = null
                    viewModel.getDoctorFilter(subdistrictsId, categoryId)
                }
                categorySelected == false -> {
                    // Only categoryId is null
                    categoryId = null
                    viewModel.getDoctorFilter(subdistrictsId, categoryId)
                }
                else -> {
                    // Both selectedId and categoryId are not null
                    // Your other logic goes here
                    viewModel.getDoctorFilter(subdistrictsId, categoryId)


                }
            }

            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }


    private fun showLoadingDialog() {
        loadingDialog = ProgressDialog(requireContext())
        loadingDialog?.setMessage("Loading...")
        loadingDialog?.setCancelable(false)
        loadingDialog?.show()
    }


}