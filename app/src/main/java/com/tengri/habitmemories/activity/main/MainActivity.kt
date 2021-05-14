package com.tengri.habitmemories.activity.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tengri.habitmemories.R
import com.tengri.habitmemories.activity.habit_detail.HabitDetailActivity
import com.tengri.habitmemories.activity.main.adapter.HabitListAdapter
import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.dialogs.HabitDialog
import com.tengri.habitmemories.state.HabitState
import com.tengri.habitmemories.state.HabitState.habits
import dev.sasikanth.colorsheet.ColorSheet
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var habitListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        initializeViews()
    }

    private fun initializeViews() {
        habitListRecyclerView = findViewById(R.id.habitList)

        // recyclerview
        Observable.fromCallable {
            return@fromCallable HabitState.getDBHabits()
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { habits ->
                habitListRecyclerView.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                habitListRecyclerView.adapter = HabitListAdapter(
                    habits,
                    this::onRowClicked,
                    this::onColorPickerClicked,
                    this::onEditButtonClicked,
                    this::onDeleteButtonClicked
                )
            }

//        addDragDrop()

        // fab
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val dialog = HabitDialog(this)
            dialog.setOnSubmit {
                HabitState.addHabit(Habit(0, it))
                habitListRecyclerView.adapter!!.notifyItemInserted(HabitState.lastIndex())
            }

            dialog.show()
        }
    }

    private fun addDragDrop() {
        // drag-drop functionality
        val simpleCallback =
            object : SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition

                    HabitState.swapIds(from, to)

                    Collections.swap(habits, from, to)

                    recyclerView.adapter!!.notifyItemMoved(from, to)

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

            }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(habitListRecyclerView)
    }

    private fun onRowClicked(position: Int) {
        val rowHabit = habits[position]

        val intent = Intent(applicationContext, HabitDetailActivity::class.java)
        intent.putExtra("habitId", rowHabit.id)

        startActivity(intent)
    }

    private fun onColorPickerClicked() {
        ColorSheet().colorPicker(
            colors = intArrayOf(
                Color.BLACK,
                Color.BLUE,
                Color.CYAN,
                Color.GRAY,
                Color.GREEN,
                Color.LTGRAY,
                Color.MAGENTA,
                Color.RED
            ),
            noColorOption = true,
            listener = { color ->
                // Handle color
            })
            .show(supportFragmentManager)
    }

    private fun onEditButtonClicked(pos: Int) {
        val adapter = habitListRecyclerView.adapter!!
        val habit = habits[pos]

        val dialog = HabitDialog(this, habit.name!!)

        dialog.setOnSubmit {
            // update habit
            habit.name = it

            // update db
            DBInterface.db.habitDao().update(habit)

            // update adapter
            adapter.notifyItemChanged(pos)
        }

        dialog.show()
    }

    private fun onDeleteButtonClicked(pos: Int) {
        val adapter = habitListRecyclerView.adapter!!
        val habit = habits[pos]

        HabitState.deleteHabit(habit)
        adapter.notifyItemRemoved(pos)
    }

}