package com.tengri.habitmemories.activity.habit_detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tengri.habitmemories.R
import com.tengri.habitmemories.database.entities.Memory
import com.tengri.habitmemories.state.MemoryState

class MemoryListAdapter(
    private val memoryList: MutableList<Memory>,
    private val onItemClicked: (pos: Int) -> Unit
) : RecyclerView.Adapter<MemoryListAdapter.ModelViewHolder>() {

    class ModelViewHolder(view: View, private val onItemClicked: (pos: Int) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val memoryContentTextView: TextView = view.findViewById(R.id.memoryContent)
        private val deleteButton: ImageButton = view.findViewById(R.id.deleteMemoryButton)

        init {
            view.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bindItems(item: Memory, memoryListAdapter: MemoryListAdapter) {
            deleteButton.setOnClickListener {
                MemoryState.deleteMemory(item)
                memoryListAdapter.notifyItemRemoved(adapterPosition)
            }

            memoryContentTextView.text = item.content
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.memory_list_item, parent, false)

        return ModelViewHolder(view, onItemClicked)
    }

    override fun getItemCount(): Int {
        return memoryList.size
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bindItems(memoryList[position], this)
    }
}