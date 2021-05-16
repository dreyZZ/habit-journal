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
import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.entities.Experience
import com.tengri.habitmemories.dialogs.ImageDialog
import com.tengri.habitmemories.state.ExperienceState

class ExperienceListAdapter(
    private val experienceList: MutableList<Experience>,
    private val onItemClicked: (pos: Int) -> Unit,
    private val onEditButtonClicked: (
        item: Experience,
        adapterPosition: Int,
        experienceListAdapter: ExperienceListAdapter
    ) -> Unit,
    private val onImageButtonClicked: (
        item: Experience,
        adapterPosition: Int,
        experienceListAdapter: ExperienceListAdapter
    ) -> Unit
) : RecyclerView.Adapter<ExperienceListAdapter.ModelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.experience_list_item, parent, false)

        return ModelViewHolder(view, onItemClicked, onEditButtonClicked, onImageButtonClicked)
    }

    override fun getItemCount(): Int {
        return experienceList.size
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bindItems(experienceList[position], this)
    }

    class ModelViewHolder(
        view: View,
        private val onItemClicked: (pos: Int) -> Unit,
        private val onEditButtonClicked: (
            item: Experience,
            adapterPosition: Int,
            experienceListAdapter: ExperienceListAdapter
        ) -> Unit,
        private val onImageButtonClicked: (
            item: Experience,
            adapterPosition: Int,
            experienceListAdapter: ExperienceListAdapter
        ) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {
        private val contentTextView: TextView = view.findViewById(R.id.experienceContent)
        private val deleteButton: ImageButton = view.findViewById(R.id.deleteExperienceButton)
        private val editButton: ImageButton = view.findViewById(R.id.editExperienceButton)
        private val imageAddButton: ImageButton = view.findViewById(R.id.addImageButton)
        private val imageView: ImageView = view.findViewById(R.id.experienceImageView)

        init {
            view.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bindItems(item: Experience, experienceListAdapter: ExperienceListAdapter) {
            deleteButton.setOnClickListener {
                ExperienceState.delete(item)
                experienceListAdapter.notifyItemRemoved(adapterPosition)
            }

            editButton.setOnClickListener {
                onEditButtonClicked(item, adapterPosition, experienceListAdapter)
            }

            imageAddButton.setOnClickListener {
                onImageButtonClicked(item, adapterPosition, experienceListAdapter)
            }

            item.image?.let { imageBytes ->
                Glide.with(this.itemView)
                    .load(imageBytes)
                    .override(300, 300)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .placeholder(ColorDrawable(Color.BLACK))
                    .into(imageView)

                imageView.setOnClickListener {
                    ImageDialog(
                        itemView.context,
                        imageBytes,
                        onImageButtonClicked = {
                            item.image = null
                            DBInterface.db.experienceDao().update(item)
                            experienceListAdapter.notifyItemChanged(adapterPosition)
                        }
                    ).show()
                }
            } ?: run {
                Glide.with(this.itemView)
                    .clear(imageView)
            }

            contentTextView.text = item.content
        }


    }
}