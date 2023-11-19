package com.bennohan.mydoctorapp.ui.profile

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bennohan.mydoctorapp.R
import com.bennohan.mydoctorapp.base.BaseActivity
import com.bennohan.mydoctorapp.data.UserDao
import com.bennohan.mydoctorapp.databinding.ActivityProfileBinding
import com.bennohan.mydoctorapp.helper.ViewBindingHelper.Companion.writeBitmap
import com.bennohan.mydoctorapp.ui.login.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.crocodic.core.api.ApiStatus
import com.crocodic.core.extension.*
import com.crocodic.core.helper.BitmapHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity :
    BaseActivity<ActivityProfileBinding, ProfileViewModel>(R.layout.activity_profile) {

    @Inject
    lateinit var userDao: UserDao
    private var filePhoto: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        observe()
        binding.btnLogout.setOnClickListener {
            logoutDialog()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnEditNama.setOnClickListener {
//            editDialog("Nama")
            convertToEditText()
        }

        binding.btnEditEmail.setOnClickListener {
            editDialog("Email")
        }

        binding.btnEditTelephone.setOnClickListener {
            editDialog("Nomor Telephone")
        }

        binding.btnEditPhoto.setOnClickListener {
            openPictureDialog()
        }

    }

    private fun convertToEditText() {
        // Get the current text from the TextView
        val currentText = binding.tvName.text.toString()

        // Create a new EditText
        val editText = EditText(this)
        editText.id = View.generateViewId() // Generate a unique ID for the EditText

        // Set the text of the EditText to the current text of the TextView
        editText.setText(currentText)

        // Set any other properties you need for the EditText

        // Replace the TextView with the EditText in the layout
        val parent = binding.tvName.parent as LinearLayout
        val index = parent.indexOfChild(binding.tvName)
        parent.removeView(binding.tvName)
        parent.addView(editText, index)

        // Now, editText is in place of textView in the layout
    }

    //TODO TES INI
    @SuppressLint("SetTextI18n")
    private fun editDialog(text: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_edit)

        val buttonSave = dialog.findViewById<Button>(R.id.btn_dialog_save)
        val etInput = dialog.findViewById<EditText>(R.id.et_input).textOf()
        val textView = dialog.findViewById<TextView>(R.id.tv_Masukan)

        textView.text = "Masukan $text Baru"

        //etInput TODO

        buttonSave.setOnClickListener {
            //TODO
        }

        dialog.show()


    }

    private fun openPictureDialog() {
        val myArray: Array<String> = arrayOf("take from camera 1", "insert from gallery 2")
        MaterialAlertDialogBuilder(this).apply {
            setItems(myArray) { _, which ->
                // The 'which' argument contains the index position of the selected item
                when (which) {
                    0 -> (this@ProfileActivity).activityLauncher.openCamera(
                        this@ProfileActivity,
                        "${this@ProfileActivity.packageName}.fileprovider"
                    ) { file, _ ->
                        uploadAvatar(file)
                    }
                    1 -> (this@ProfileActivity).activityLauncher.openGallery(
                        this@ProfileActivity
                    ) { file, _ ->
                        uploadAvatar(file)
                    }
                }
            }
        }.create().show()
    }

    private fun uploadAvatar(file: File?) {
        if (file == null) {
            binding.root.snacked("error")
            return
        }

        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val resizeBitmap = BitmapHelper.resizeBitmap(bitmap, 512f)

        file.delete()

        //Result Photo
        val uploadFile = this.createImageFile().also { it.writeBitmap(resizeBitmap) }

        //Processing the photo result
        filePhoto = uploadFile
        binding?.btnSave?.visibility = View.VISIBLE
        Log.d("cek isi photo", uploadFile.toString())

        if (uploadFile != null) {
            binding.ivProfileNew?.visibility = View.VISIBLE
//            binding.btnEditProfile.visibility = View.VISIBLE
            binding.ivProfileNew.let {
                Glide
                    .with(this)
                    .load(uploadFile)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .apply(RequestOptions.circleCropTransform())
                    .into(it)
            }
        } else {
            binding.root.snacked("Ungah Gambar Gaga, Coba Lagi")
//            binding?.ivUserEditedView?.visibility = View.GONE
        }


    }


    private fun logoutDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_logout)

        val buttonLogout = dialog.findViewById<Button>(R.id.btn_dialog_logout)
        val buttonCancel = dialog.findViewById<Button>(R.id.btn_dialog_cancel)
//
        buttonLogout.setOnClickListener {
            viewModel.logout()
        }

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()


    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.apiResponse.collect {
                        when (it.status) {
                            //TODO Loading dialog at fragment
                            ApiStatus.LOADING -> {}
                            ApiStatus.SUCCESS -> {
                                when (it.message) {
                                    "Profile Edited" -> {
                                        binding.root.snacked("Profile Edited")

                                    }
                                    "Logout" -> {
                                        openActivity<LoginActivity> {
                                            userDao.deleteAll()
                                            finishAffinity()
                                        }
                                    }
                                }
                                //TODO Replace it with TOAST
                            }
                            ApiStatus.ERROR -> {
//                                loadingDialog.setResponse(it.message ?: return@collect)

                            }
                            else -> binding.root.snacked("error")
                        }

                    }
                }
                launch {
                    userDao.getUser().collectLatest { user ->
                        binding.user = user

                    }
                }
            }
        }

    }
}