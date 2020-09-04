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

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.FragmentOverviewBinding
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.MarsApiFilter

/**
 * This fragment shows the the status of the Mars real-estate web services transaction.
 */
class OverviewFragment : Fragment() {

    /**
     * Lazily initialize our [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProvider(this).get(OverviewViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
//        val binding = FragmentOverviewBinding.inflate(inflater)
        val binding = FragmentOverviewBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Set the PhotosGrid recyclerView adapter to PhotoGridAdapter
        binding.photosGrid.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
            // pass lambda as a parameter. 'it' - is the argument of passed lambda
            // viewModel changes the value of _navigateToSelectedProperty MutableLiveData to the clicked MarsProperty
            // fragment observes the changes
            viewModel.displayPropertyDetails(it)
        })

        // observe the changes in the navigateToSelectedProperty event
        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner) {
            // if the property was clicked then it was passed to the navigateToSelectedProperty, so it is not null
            if (null != it) {
                // navigating to DetailFragment and passing MarsProperty as an argument using safeArgs plugin
                this.findNavController().navigate(OverviewFragmentDirections.actionShowDetail(it))
                // need to call the method to prevent unwanted navigations
                viewModel.displayPropertyDetailsComplete()
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Inflates the overflow menu that contains filtering options.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // fetches properties from server depending on the filter
        viewModel.updateFilter(
                when (item.itemId) {
                    R.id.show_buy_menu -> MarsApiFilter.SHOW_BUY // if buy menu is clicked
                    R.id.show_rent_menu -> MarsApiFilter.SHOW_RENT // if rent menu is clicked
                    else -> MarsApiFilter.SHOW_ALL
                }
        )
        return true
    }

}
