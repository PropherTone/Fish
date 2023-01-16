package com.protone.layout.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.protone.common.context.newLayoutInflater
import com.protone.common.routerPath.LayoutRouterPath
import com.protone.fishmodule_layout.databinding.ActivityListBinding
import com.protone.fishmodule_layout.databinding.ListItemLayoutBinding


@Route(path = LayoutRouterPath.List)
class ListActivity : AppCompatActivity() {

    private val binding by lazy { ActivityListBinding.inflate(newLayoutInflater) }
    private val data by lazy { mutableListOf<String>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button.setOnClickListener {
            val toList = (0..50).map { "$it" }.toList()
            data.clear()
            data.addAll(toList)
        }

        binding.list.apply {
            layoutManager =
                LinearLayoutManager(this@ListActivity, LinearLayoutManager.VERTICAL, false)
            adapter = object : RecyclerView.Adapter<Holder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
                    Holder(ListItemLayoutBinding.inflate(newLayoutInflater, parent, false))

                override fun onBindViewHolder(holder: Holder, position: Int) {
                    if (data.isEmpty()) {
                        holder.binding.text.text = "empty"
                        return
                    }
                    holder.binding.text.text = data[position]
                }

                override fun getItemCount(): Int = 100

            }
        }
    }

    class Holder(val binding: ListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

}