package com.example.fooddeliveryapp.ui.restaurantadd

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.fooddeliveryapp.R
import com.example.fooddeliveryapp.databinding.FragmentRestaurantAddBinding
import com.example.fooddeliveryapp.utils.Resource
import com.example.fooddeliveryapp.utils.gone
import com.example.fooddeliveryapp.utils.show
import java.text.SimpleDateFormat
import java.util.*
import com.zeeshan.material.multiselectionspinner.MultiSelectionSpinner
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestaurantAddFragment : Fragment() {

    private lateinit var _binding : FragmentRestaurantAddBinding
    private val viewModel : RestaurantAddViewModel by viewModels()
    private lateinit var imageUrl : String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantAddBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.apply {
            displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM

            setHomeAsUpIndicator(R.drawable.ic_back)
            setDisplayHomeAsUpEnabled(true)
        }

        addListeners()
        initializeCitySpinner()
        initializeCuisineSpinner()
        initializePaymentMethodsSpinner()
    }

    private fun addListeners() {
        _binding.addRestaurantImageView.setOnClickListener {
            addRestaurantImage()
        }

        _binding.restaurantOpenHourEditText.setOnClickListener {
            setRestaurantOpenHour()
        }

        _binding.restaurantCloseHourEditText.setOnClickListener {
            setRestaurantCloseHour()
        }

        _binding.addRestaurantButton.setOnClickListener {
            addRestaurant()
        }
    }

    private fun initializeCitySpinner() {
        val cities = resources.getStringArray(R.array.Cities)
        val adapter = ArrayAdapter(
            activity as AppCompatActivity,
            android.R.layout.simple_spinner_dropdown_item,
            cities
        )
        _binding.citySpinner.adapter = adapter
    }

    private fun initializeCuisineSpinner() {
        val cities = resources.getStringArray(R.array.Cuisines)
        val adapter = ArrayAdapter(
            activity as AppCompatActivity,
            android.R.layout.simple_spinner_dropdown_item,
            cities
        )
        _binding.cuisineSpinner.adapter = adapter
    }

    private fun initializePaymentMethodsSpinner() {
       val paymentMethods = resources.getStringArray(R.array.RestaurantPaymentMethods).toList()
        _binding.multiSelectionSpinner.items = paymentMethods
    }

    private fun addRestaurantImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startForResult.launch(intent)
    }

    private fun setRestaurantOpenHour() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            _binding.restaurantOpenHourEditText.setText(SimpleDateFormat("HH:mm", Locale.US).format(cal.time))
        }
        TimePickerDialog(
            activity,
            R.style.DialogTheme,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun setRestaurantCloseHour() {
        val cal = Calendar.getInstance()
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            _binding.restaurantCloseHourEditText.setText(SimpleDateFormat("HH:mm", Locale.US).format(cal.time))
        }
        TimePickerDialog(
            activity,
            R.style.DialogTheme,
            timeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun addRestaurant(){
        val name = _binding.restaurantNameEditText.editText?.text.toString()
        val cuisine = _binding.cuisineSpinner.selectedItem.toString()
        val deliveryInfo = _binding.restaurantDeliveryInfoEditText.editText?.text.toString()
        val deliveryTime = _binding.restaurantDeliveryTimeEditText.text.toString()
        val address = _binding.restaurantAddressEditText.editText?.text.toString()
        val district = _binding.citySpinner.selectedItem.toString()
        val minDeliveryFee = _binding.restaurantDeliveryFeeEditText.text.toString()
        val paymentMethods = _binding.multiSelectionSpinner.selectedItems.joinToString("-")
        val phone = _binding.restaurantPhoneEditText.editText?.text.toString()
        val website = _binding.restaurantWebsiteEditText.editText?.text.toString()

        viewModel.addRestaurant(name, cuisine,deliveryInfo, deliveryTime,
            imageUrl,address, district, minDeliveryFee, paymentMethods, phone, website)
            .observe(viewLifecycleOwner, {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        Log.i(RestaurantAddFragment::class.java.name, it.message.toString())
                        _binding.progressBar.show()
                    }
                    Resource.Status.SUCCESS -> {
                        Log.i(RestaurantAddFragment::class.java.name, it.message.toString())
                        _binding.progressBar.gone()
                        findNavController().navigate(R.id.action_restaurantAddFragment_to_homeFragment)
                    }
                    Resource.Status.ERROR -> {
                        Log.e(RestaurantAddFragment::class.java.name, it.message.toString())
                        _binding.progressBar.gone()
                        Toast.makeText(context, "Operation Failed", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.action_restaurantAddFragment_to_homeFragment)
                    }
                }
            })
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImage: Uri = result.data?.data!!
                Glide.with(requireContext())
                    .load(selectedImage)
                    .fitCenter()
                    .into(_binding.addRestaurantImageView)

                //TODO : If image couldn't uploaded??
                imageUrl = viewModel.uploadImage(selectedImage)

            }
        }

    /*
    //TODO : Boşluğa tıklandığında klavyeyi saklamak için
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }
    */

}