/*
 *  Copyright 2018, The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.marsrealestate.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.detail.DetailFragment
import com.example.android.marsrealestate.network.MarsProperty

/**
 * The [ViewModel] that is associated with the [DetailFragment].
 */
class DetailViewModel(marsProperty: MarsProperty, app: Application) : AndroidViewModel(app) {

    /**
     * Encapsulated property to be observed by DataBinding
     */
    private val _selectedProperty = MutableLiveData<MarsProperty>()
    val selectedProperty: LiveData<MarsProperty>
        get() = _selectedProperty

    /**
     * Initializes the selected property to the one passed as a parameter
     */
    init {
        _selectedProperty.value = marsProperty
    }

    /**
     * Transforms price of the selected MarsProperty to be correctly displayed depending on the
     * isRental property. Uses prepared templates in string.xml
     */
    val displayPropertyPrice = Transformations.map(selectedProperty) {
        app.applicationContext.getString(
                when (it.isRental) {
                    true -> R.string.display_price_monthly_rental // template for rental properties
                    false -> R.string.display_price // template for properties for sale
                },
                it.price) // need second parameter because it is defined in the string.xml template
    }

    val displayPropertyType = Transformations.map(selectedProperty) {
        app.applicationContext.getString(
                R.string.display_type, // template for the display type
                app.applicationContext.getString( // need to be called the second time to fetch strings from resources because the template needs two parameters
                        when (it.isRental) {
                            true -> R.string.type_rent // if the type is rent
                            false -> R.string.type_sale // if the type is sale
                        }
                )
        )
    }
}
