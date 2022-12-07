package com.protone.layout.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.protone.common.context.newLayoutInflater
import com.protone.fishmodule_layout.databinding.GithubHotItemLayoutBinding
import com.protone.layout.entity.Item

class GithubPagingAdapter :
    PagingDataAdapter<Item, GithubPagingAdapter.Holder>(object : DiffUtil.ItemCallback<Item>() {

        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.name == newItem.name
        }

    }) {

    open class Holder(val binding: GithubHotItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            getItem(position)?.let {
                name.text = it.name
                description.text = it.description
                star.text = it.stargazers_count.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            GithubHotItemLayoutBinding.inflate(
                parent.context.newLayoutInflater,
                parent,
                false
            )
        )
    }

}