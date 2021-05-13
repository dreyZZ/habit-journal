package com.tengri.habitmemories.activity.habit_detail.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tengri.habitmemories.R
import com.tengri.habitmemories.database.entities.Memory
import com.tengri.habitmemories.dialogs.ImageDialog
import com.tengri.habitmemories.state.MemoryState

class MemoryListAdapter(
    private val memoryList: MutableList<Memory>,
    private val onItemClicked: (pos: Int) -> Unit,
    private val onEditButtonClicked: (
        item: Memory,
        adapterPosition: Int,
        memoryListAdapter: MemoryListAdapter
    ) -> Unit,
    private val onImageButtonClicked: (
        item: Memory,
        adapterPosition: Int,
        memoryListAdapter: MemoryListAdapter
    ) -> Unit
) : RecyclerView.Adapter<MemoryListAdapter.ModelViewHolder>() {

    class ModelViewHolder(
        view: View,
        private val onItemClicked: (pos: Int) -> Unit,
        private val onEditButtonClicked: (
            item: Memory,
            adapterPosition: Int,
            memoryListAdapter: MemoryListAdapter
        ) -> Unit,
        private val onImageButtonClicked: (
            item: Memory,
            adapterPosition: Int,
            memoryListAdapter: MemoryListAdapter
        ) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {
        private val memoryContentTextView: TextView = view.findViewById(R.id.memoryContent)
        private val deleteButton: ImageButton = view.findViewById(R.id.deleteMemoryButton)
        private val editButton: ImageButton = view.findViewById(R.id.editMemoryButton)
        private val imageAddButton: ImageButton = view.findViewById(R.id.addImageButton)
        private val image: ImageView = view.findViewById(R.id.memoryImageView)

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

            editButton.setOnClickListener {
                onEditButtonClicked(item, adapterPosition, memoryListAdapter)
            }

            imageAddButton.setOnClickListener {
                onImageButtonClicked(item, adapterPosition, memoryListAdapter)
            }

            item.image?.let { imageBytes ->
                Glide.with(this.itemView)
                    .load(imageBytes)
                    .override(300, 300)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(ColorDrawable(Color.BLACK))
                    .into(image)

                image.setOnClickListener {
                    ImageDialog(itemView.context, imageBytes).show()
                }
            }

            memoryContentTextView.text = item.content
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.memory_list_item, parent, false)

        return ModelViewHolder(view, onItemClicked, onEditButtonClicked, onImageButtonClicked)
    }

    override fun getItemCount(): Int {
        return memoryList.size
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bindItems(memoryList[position], this)
    }
}