package com.assignment.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.base.network.MainRepository
import com.assignment.home.modal.ModelMedia
import kotlinx.coroutines.launch

class ExploreMediaViewModal   : ViewModel() {
     private val repository = MainRepository()
     val modalMedia = MutableLiveData<List<ModelMedia>?>()
     val statusError = MutableLiveData<String>()

    fun fetchDataFromServer() {
        viewModelScope.launch {
            try {
                val response = repository.hitGetMedia()
                if (response.isSuccessful) {
                    //success:
                    modalMedia.postValue(response.body())
                } else {
                    //error:
                    statusError.postValue(response.message());
                }
            } catch (e: Exception) {
                //exception:
                statusError.postValue(e.message);
            }
        }
    }
}