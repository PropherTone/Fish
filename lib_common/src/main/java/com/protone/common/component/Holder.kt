package com.protone.common.component

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

open class Holder<DataBinding : ViewDataBinding>(val binding: DataBinding) :
    RecyclerView.ViewHolder(binding.root)