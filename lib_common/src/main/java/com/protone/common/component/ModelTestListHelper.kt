package com.protone.common.component

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.protone.common.context.newLayoutInflater
import com.protone.common.databinding.GuideItemBinding

class ModelTestListHelper<T> {

    private val guideList by lazy { mutableListOf<Pair<String, T>>() }

    fun add(string: String, objects: T): ModelTestListHelper<T> {
        guideList.add(Pair(string, objects))
        return this
    }

    fun init(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager,
        interval: Int,
        block: (T) -> Unit
    ) {
        recyclerView.apply {
            this.layoutManager = layoutManager
            setHasFixedSize(true)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val position = parent.getChildLayoutPosition(view)
                    outRect.top = interval
                    outRect.left = interval
                    if (position % 2 != 0) {
                        outRect.right = interval
                    }
                }
            })
            adapter = object : RecyclerView.Adapter<GuideViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GuideViewHolder {
                    return GuideViewHolder(GuideItemBinding.inflate(recyclerView.context.newLayoutInflater))
                }

                override fun onBindViewHolder(holder: GuideViewHolder, position: Int) {
                    holder.binding.guideButton.apply {
                        text = guideList[position].first
                        setOnClickListener {
                            block(guideList[position].second)
                        }
                    }
                }

                override fun getItemCount(): Int = guideList.size

            }
        }
    }

    class GuideViewHolder(val binding: GuideItemBinding) : RecyclerView.ViewHolder(binding.root)
}