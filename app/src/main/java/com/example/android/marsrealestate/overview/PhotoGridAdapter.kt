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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.marsrealestate.databinding.GridViewItemBinding
import com.example.android.marsrealestate.network.MarsProperty

/**
 * RecyclerView adapter that extends ListAdapter to show all downloaded MarsProperties in a list
 */
class PhotoGridAdapter(val onClickListener: OnClickListener) : ListAdapter<MarsProperty, PhotoGridAdapter.MarsPropertyViewHolder>(DiffCallback) {
    /**
     * Needs no reference to the PhotoGridAdapter therefore is defined as a companion object
     * Implements DiffUtil.ItemCallback<MarsProperty>
     */
    companion object DiffCallback : DiffUtil.ItemCallback<MarsProperty>() {
        /**
         * Checks if items are in fact references to the same objects
         */
        override fun areItemsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem === newItem
        }

        /**
         * Checks if the contents of the items are the same using retrieved IDs
         */
        override fun areContentsTheSame(oldItem: MarsProperty, newItem: MarsProperty): Boolean {
            return oldItem.id == newItem.id
        }

    }

    /**
     * Uses GridViewBinding as private property to enable databinding
     * Need to pass binding.root to the RecyclerView.ViewHolder
     */
    class MarsPropertyViewHolder(private var binding: GridViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds the property to the XML using DataBinding
         * Need to execute all pending bindings to enable correct representation
         */
        fun bind(marsProperty: MarsProperty) {
            binding.property = marsProperty
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGridAdapter.MarsPropertyViewHolder {
        return MarsPropertyViewHolder(GridViewItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: PhotoGridAdapter.MarsPropertyViewHolder, position: Int) {
        // get the correct item depending on its position
        val marsProperty = getItem(position)
        // set the clickListener on the itemView
        holder.itemView.setOnClickListener {
            // marsProperty in the correct position is passed as an argument to the listener's onClick method
            onClickListener.onClick(marsProperty)
        }
        holder.bind(marsProperty)
    }


    /**
     * Internal class that is used as a listener for clicks on the item in the RecyclerView
     * Accepts a function as a parameter that has one argument of type MarsProperty and returns Unit
     */
    class OnClickListener(val clickListener: (marsProperty: MarsProperty) -> Unit) {
        /**
         * Passes its argument of type MarsProperty to the class' clickListener property function
         */
        fun onClick(marsProperty: MarsProperty) = clickListener(marsProperty)
    }
}