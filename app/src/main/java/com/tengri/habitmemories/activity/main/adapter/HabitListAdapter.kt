package com.tengri.habitmemories.activity.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.tengri.habitmemories.R
import com.tengri.habitmemories.database.entities.Habit


class HabitListAdapter(
    private val habitList: MutableList<Habit>,
    private val onRowClicked: (Int) -> Unit,
    private val onColorPickerSelected: () -> Unit,
    private val onEditClicked: (Int) -> Unit,
    private val onDeleteClicked: (Int) -> Unit
) : RecyclerView.Adapter<HabitListAdapter.ModelViewHolder>() {

    private val viewBinderHelper = ViewBinderHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.habit_list_item, parent, false)

        return ModelViewHolder(
            view,
            onRowClicked,
            onColorPickerSelected,
            onEditClicked,
            onDeleteClicked
        )
    }

    override fun getItemCount(): Int {
        return habitList.size
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        viewBinderHelper.bind(holder.swipeLayout, habitList[position].id.toString())
        viewBinderHelper.setOpenOnlyOne(true)

        holder.bindItems(habitList[position], this)
    }

    class ModelViewHolder(
        view: View,
        private val onRowClicked: (Int) -> Unit,
        private val onColorPickerSelected: () -> Unit,
        private val onEditClicked: (Int) -> Unit,
        private val onDeleteClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val habitTextView: TextView = view.findViewById(R.id.habitName)
        private val colorPickerButton: ImageButton = view.findViewById(R.id.colorPickerButton)
        private val editHabitButton: ImageView = view.findViewById(R.id.editHabitButton)
        private val deleteHabitButton: ImageView = view.findViewById(R.id.deleteHabitButton)
        val swipeLayout: SwipeRevealLayout = view.findViewById(R.id.swipeLayout)

        init {
            habitTextView.setOnClickListener {
                onRowClicked(adapterPosition)
            }

            colorPickerButton.setOnClickListener {
                onColorPickerSelected()
            }

            editHabitButton.setOnClickListener {
                onEditClicked(adapterPosition)
                swipeLayout.close(true)
            }

            deleteHabitButton.setOnClickListener {
                onDeleteClicked(adapterPosition)
                swipeLayout.close(true)
            }
        }

        fun bindItems(item: Habit, habitListAdapter: HabitListAdapter) {
            habitTextView.text = item.name
        }
    }
}