package com.protone.common.component

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
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
        recyclerView.apply recyclerView@{
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
            ItemTouchHelper(ModelItemTouchHelper(object : ITouch {
                override fun onMove(
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) {
                    this@recyclerView.adapter
                        ?.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                }

            })).apply {
                attachToRecyclerView(this@recyclerView)
            }

        }
    }

    class GuideViewHolder(val binding: GuideItemBinding) : RecyclerView.ViewHolder(binding.root)

    inner class ModelItemTouchHelper(private val iTouch: ITouch) : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeMovementFlags(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            iTouch.onMove(viewHolder, target)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            iTouch.onSwiped(viewHolder, direction)
        }

    }

    interface ITouch {
        fun onMove(
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        )

        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    }

}