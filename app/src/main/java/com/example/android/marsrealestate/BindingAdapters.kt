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

package com.example.android.marsrealestate

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.MarsApiStatus
import com.example.android.marsrealestate.overview.PhotoGridAdapter

/**
 * Binding adapter that allows to show loading animation or error connection image
 * Depending on the status of the MarsApiStatus
 */
@BindingAdapter("marsApiStatus")
fun ImageView.bindStatus(status: MarsApiStatus?) {
    when(status) {
        MarsApiStatus.LOADING -> {
            this.visibility = View.VISIBLE
            this.setImageResource(R.drawable.loading_animation)
        }
        MarsApiStatus.DONE -> {
            this.visibility = View.GONE
        }
        MarsApiStatus.ERROR -> {
            this.visibility = View.VISIBLE
            this.setImageResource(R.drawable.ic_connection_error)
        }
    }
}

/**
 * Binding adapter extension function on RecyclerView to submit a list
 * to the recyclerView adapter.
 * Recycler view that has attribute called: listData gets the list
 * from viewModel using DataBinding and submits it to the adapter
 */
@BindingAdapter("listData")
fun RecyclerView.bindRecyclerView(data: List<MarsProperty>?) {
    val adapter = this.adapter as PhotoGridAdapter
    adapter.submitList(data)
}

/**
 * Adapter to load image into an imageView
 * Needs a URL with image
 * This binding adapter is executed when an ImageView item has imageUrl attribute
 */
@BindingAdapter("imageUrl")
fun ImageView.bindImage(imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        // glide lets us download and cache the image on a background thread
        Glide.with(this.context)
                .load(imgUri)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation) // placeholder image to be shown while the real one is downloading
                        .error(R.drawable.ic_broken_image)) // is shown in case some error happens
                .into(this) // this is the ImageView
    }
}