package com.tengri.habitmemories.activity.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.tengri.habitmemories.R
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.util.defaultRowColor


class HabitListAdapter(
    private val habitList: MutableList<Habit>,
    private val onRowClicked: (Int) -> Unit,
    private val onColorPickerSelected: (Int, View) -> Unit,
    private val onEditClicked: (Int) -> Unit,
    private val onDeleteClicked: (Int) -> Unit
) : RecyclerView.Adapter<HabitListAdapter.ModelViewHolder>(), Filterable {

    private var filteredHabits: MutableList<Habit> = habitList
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
        return filteredHabits.size
    }

    override fun onBindViewHolder(holder: ModelViewHolder, position: Int) {
        viewBinderHelper.bind(holder.swipeLayout, filteredHabits[position].id.toString())
        viewBinderHelper.setOpenOnlyOne(true)

        holder.bindItems(filteredHabits[position], this)
    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults? {
                filteredHabits = habitList
                when (charSequence) {
                    "clear" -> {
                    }
                    else -> {
                        val colorCode = Integer.parseInt(charSequence.toString())
                        filteredHabits = if (colorCode == -1) {
                            filteredHabits.filter {
                                it.color == null
                            }.toMutableList()
                        } else {
                            filteredHabits.filter {
                                it.color == colorCode
                            }.toMutableList()
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredHabits
                return filterResults
            }

            override fun publishResults(
                charSequence: CharSequence?,
                filterResults: FilterResults
            ) {
                filteredHabits = filterResults.values as MutableList<Habit>
                notifyDataSetChanged()
            }
        }
    }

    class ModelViewHolder(
        view: View,
        private val onRowClicked: (Int) -> Unit,
        private val onColorPickerSelected: (Int, View) -> Unit,
        private val onEditClicked: (Int) -> Unit,
        private val onDeleteClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val habitTextView: TextView = view.findViewById(R.id.habitName)
        private val colorPickerButton: ImageButton = view.findViewById(R.id.colorPickerButton)
        private val editHabitButton: ImageView = view.findViewById(R.id.editHabitButton)
        private val deleteHabitButton: ImageView = view.findViewById(R.id.deleteHabitButton)
        val swipeLayout: SwipeRevealLayout = view.findViewById(R.id.swipeLayout)
        private val foreground: View = view.findViewById(R.id.rowFG)


        init {
            habitTextView.setOnClickListener {
                onRowClicked(adapterPosition)
            }

            colorPickerButton.setOnClickListener {
                onColorPickerSelected(adapterPosition, foreground)
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

            if (item.color != null) {
                foreground.setBackgroundColor(item.color!!)
            } else {
                foreground.setBackgroundColor(defaultRowColor)
            }
        }
    }
}