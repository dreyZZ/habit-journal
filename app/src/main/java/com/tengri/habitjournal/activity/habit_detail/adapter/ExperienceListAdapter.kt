package com.tengri.habitjournal.activity.habit_detail.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.tengri.habitjournal.R
import com.tengri.habitjournal.database.DBInterface
import com.tengri.habitjournal.database.entities.Experience
import com.tengri.habitjournal.dialogs.ImageDialog
import com.tengri.habitjournal.state.ExperienceState
import java.io.File
import java.lang.Exception
import java.util.*

class ExperienceListAdapter(
    val experienceList: MutableList<Experience>,
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

    var editModeChange: Boolean = false
    var isEditModeEnabled = false
    lateinit var mItemTouchHelper: ItemTouchHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.experience_list_item, parent, false)

        return ModelViewHolder(view, onItemClicked, onEditButtonClicked, onImageButtonClicked)
    }

    override fun getItemCount(): Int {
        return experienceList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        if (!editModeChange) {
            holder.bindItems(experienceList[position], this)
        }

        holder.dragButton.setOnTouchListener { _, _ ->
            mItemTouchHelper.startDrag(holder)
            false
        }

        when (isEditModeEnabled) {
            true -> holder.dragButton.visibility = View.VISIBLE
            false -> holder.dragButton.visibility = View.GONE
        }

        editModeChange = false
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
        private val dateTextView: TextView = view.findViewById(R.id.experienceDate)
        val dragButton: ImageButton = view.findViewById(R.id.dragButton)

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

            item.insertDate?.let {
                dateTextView.text = Date(it).toLocaleString()
            } ?: run {
                dateTextView.visibility = View.GONE
            }

            item.image?.let { imageUri ->
                try {
                    imageView.visibility = View.VISIBLE

                    val imageFile = File(imageUri)

                    Glide.with(imageView)
                        .load(Uri.fromFile(imageFile))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .override(300, 300)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .placeholder(ColorDrawable(Color.BLACK))
                        .into(imageView)

                    imageView.setOnClickListener {
                        ImageDialog(
                            itemView.context,
                            imageUri,
                            onDeleteButtonClicked = {
                                imageFile.delete()
                                item.image = null
                                DBInterface.db.experienceDao().update(item)
                                experienceListAdapter.notifyItemChanged(adapterPosition)
                            }
                        ).show()
                    }
                } catch (e: Exception) {
                    imageView.visibility = View.GONE
                }
            } ?: run {
                Glide.with(this.itemView)
                    .clear(imageView)
            }

            contentTextView.text = item.content
        }
    }

}