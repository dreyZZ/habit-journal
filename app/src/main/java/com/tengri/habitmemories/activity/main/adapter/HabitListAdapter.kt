package com.tengri.habitmemories.activity.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tengri.habitmemories.R
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.state.HabitState

class HabitListAdapter(
    private val habitList: MutableList<Habit>
) : RecyclerView.Adapter<HabitListAdapter.ModelViewHolder>() {

    class ModelViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        private val habitTextView: TextView = view.findViewById(R.id.habitName)

        init {
        }

        fun bindItems(item: Habit, habitListAdapter: HabitListAdapter) {
            habitTextView.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModelViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.habit_list_item, parent, false)

        return ModelViewHolder(view)
    }

    override fun getItemCount(): Int {
        return habitList.size
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        holder.bindItems(habitList[position], this)
    }
}