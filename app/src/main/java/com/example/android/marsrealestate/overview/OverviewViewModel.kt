/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.MarsApi
import com.example.android.marsrealestate.network.MarsApiFilter
import com.example.android.marsrealestate.network.MarsProperty
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Enum class for different statuses of MarsApi
 */
enum class MarsApiStatus { LOADING, DONE, ERROR }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
 */
class OverviewViewModel : ViewModel() {

    /**
     * Job to enable creating specific coroutine scope and cancelling all child coroutines in it
     * if necessary
     */
    private var viewModelJob = Job()

    /**
     * Specific coroutine scope to allow updating UI thread and cancel all viewModel coroutines if necessary
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _status = MutableLiveData<MarsApiStatus>()

    // The external immutable LiveData for the request status String
    val status: LiveData<MarsApiStatus>
        get() = _status

    /**
     * The internal MutableLiveData that stores a list of MarsProperties
     */
    private val _properties = MutableLiveData<List<MarsProperty>>()

    /**
     * The external immutable LiveData that stores the list of MarsProperties
     */
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    /**
     * Encapsulated property to track navigation to the DetailFragment
     */
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    /**
     * Initiates navigation to the DetailFragment
     */
    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }

    /**
     * Used to prevent unwanted extra navigations when our navigation has happened
     */
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
        getMarsRealEstateProperties(MarsApiFilter.SHOW_ALL)
    }

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    private fun getMarsRealEstateProperties(filter: MarsApiFilter) {
        // use coroutine to run the code asynchronously
        uiScope.launch {
            try {
                // at the beginning the status is LOADING
                _status.value = MarsApiStatus.LOADING
                // non-blocking call to get the result
                // once the result is available set it to the response.value
                val listResult = MarsApi.retrofitService.getPropertiesAsync(filter.value)

                //at the end the status is DONE
                _status.value = MarsApiStatus.DONE
                // set the properties value to the retrieved list
                _properties.value = listResult
            } catch (e: Exception) {
                // in case of error statue is ERROR
                _status.value = MarsApiStatus.ERROR
                //clear the properties list
                _properties.value = ArrayList()
            }
        }
    }

    /**
     * Cancel any viewModel coroutines in the method to prevent them from running even after
     * the ViewModel is destroyed
     */
    override fun onCleared() {
        super.onCleared()
        uiScope.cancel()
    }

    /**
     * Re-fetches properties from Server applying filter passed as a parameter
     */
    fun updateFilter(marsApiFilter: MarsApiFilter) {
        getMarsRealEstateProperties(marsApiFilter)
    }
}
