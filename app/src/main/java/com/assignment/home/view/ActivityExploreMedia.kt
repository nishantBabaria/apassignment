package com.assignment.home.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.assignment.R
import com.assignment.base.utils.CommonUtils
import com.assignment.databinding.ActivityExploreMediaBinding
import com.assignment.home.viewmodel.ExploreMediaViewModal

class ActivityExploreMedia : AppCompatActivity() {

    private var binding: ActivityExploreMediaBinding? = null
    private lateinit var viewModel: ExploreMediaViewModal
    private var adapter : AdapterExploreMedia? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExploreMediaBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        viewModel = ViewModelProvider(this)[ExploreMediaViewModal::class.java]

        intAdapter()
        initObserver()
        initApi()

        //click listener
        binding?.textRetry?.setOnClickListener { clickRetry() }
        binding?.toolbar?.setNavigationOnClickListener { onBackPressed() }

    }

    //hit api
    private fun initApi() {
       if (CommonUtils.isInternetAvailable(this)) {
           viewModel.fetchDataFromServer()
        }else{
            showError(getString(R.string.no_internet))
       }
    }

    //register observer for render data after api response
    private fun initObserver() {
        //success
        viewModel.modalMedia.observe(this) {
            showError(error = "")
            adapter?.updateData(it);
        }

        //error
        viewModel.statusError.observe(this) { error ->
            showError(error = error)
        }
    }

    //recycler view adapter
    private fun intAdapter() {
        binding?.list?.layoutManager = GridLayoutManager(this, 3)
        adapter = AdapterExploreMedia()
        binding?.list?.adapter = adapter
    }

    //manage api error status
    private fun showError(error: String) {
        if(error.isNotEmpty()){
            binding?.constraintStatus?.visibility = View.VISIBLE
            binding?.textError?.visibility = View.VISIBLE
            binding?.textRetry?.visibility = View.VISIBLE
            binding?.progressBar?.visibility = View.GONE
            binding?.textError?.text = error
        }else{
            binding?.constraintStatus?.visibility = View.GONE
        }
    }


    //region common method

    private fun showToast(message: String = "") {
        if (message.isNotEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    //endregion


    //region click method

    private fun clickRetry() {
        if (CommonUtils.isInternetAvailable(this)) {
            binding?.textError?.visibility = View.GONE
            binding?.textRetry?.visibility = View.GONE
            binding?.progressBar?.visibility = View.VISIBLE
            viewModel.fetchDataFromServer()
        }else{
            showToast(getString(R.string.no_internet))
        }
    }

    //endregion

}