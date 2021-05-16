package com.tengri.habitmemories.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
import com.tengri.habitmemories.activity.settings.SettingsActivity
import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.dialogs.HabitDialog
import com.tengri.habitmemories.state.HabitState
import com.tengri.habitmemories.state.HabitState.habits
import com.tengri.habitmemories.util.rowColors
import dev.sasikanth.colorsheet.ColorSheet
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mHabitListRecyclerView: RecyclerView
    private lateinit var mHabitListAdapter: HabitListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        initializeViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                ColorSheet().colorPicker(
                    colors = rowColors,
                    noColorOption = true,
                    listener = { color ->
                        mHabitListAdapter.filter!!.filter(color.toString())
                    })
                    .show(supportFragmentManager)
                true
            }
            R.id.action_clear_filters -> {
                mHabitListAdapter.filter!!.filter("clear")
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeViews() {
        mHabitListRecyclerView = findViewById(R.id.habitList)

        // recyclerview
        Observable.fromCallable {
            return@fromCallable HabitState.getDBHabits()
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { habits ->
                mHabitListRecyclerView.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                mHabitListAdapter = HabitListAdapter(
                    habits,
                    this::onRowClicked,
                    this::onColorPickerClicked,
                    this::onEditButtonClicked,
                    this::onDeleteButtonClicked
                )
                mHabitListRecyclerView.adapter = mHabitListAdapter
            }

        addDragDrop()

        // fab
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val dialog = HabitDialog(this)
            dialog.setOnSubmit {
                HabitState.addHabit(Habit(0, it))
                mHabitListAdapter.notifyItemInserted(HabitState.lastIndex())
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
        itemTouchHelper.attachToRecyclerView(mHabitListRecyclerView)
    }

    private fun onRowClicked(position: Int) {
        val rowHabit = habits[position]

        val intent = Intent(applicationContext, HabitDetailActivity::class.java)
        intent.putExtra("habitId", rowHabit.id)

        startActivity(intent)
    }

    private fun onColorPickerClicked(pos: Int, foreground: View) {
        val adapter = mHabitListAdapter
        val habit = habits[pos]

        ColorSheet().colorPicker(
            colors = rowColors,
            noColorOption = true,
            listener = { color ->
                if (color != -1) {
                    habit.color = color

                    DBInterface.db.habitDao().update(habit)
                } else {
                    habit.color = null

                    DBInterface.db.habitDao().update(habit)
                }

                adapter.notifyItemChanged(pos)
            })
            .show(supportFragmentManager)
    }

    private fun onEditButtonClicked(pos: Int) {
        val adapter = mHabitListAdapter
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
        val adapter = mHabitListAdapter
        val habit = habits[pos]

        HabitState.deleteHabit(habit)
        adapter.notifyItemRemoved(pos)
    }

}