package com.tengri.habitmemories.activity.main

import android.content.Intent
import android.graphics.Color
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
import com.tengri.habitmemories.App
import com.tengri.habitmemories.R
import com.tengri.habitmemories.activity.habit_detail.HabitDetailActivity
import com.tengri.habitmemories.activity.main.adapter.HabitListAdapter
import com.tengri.habitmemories.activity.settings.SettingsActivity
import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.entities.Experience
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.dialogs.HabitDialog
import com.tengri.habitmemories.state.AppSettings
import com.tengri.habitmemories.state.HabitState
import com.tengri.habitmemories.util.convertDrawableToByteArray
import com.tengri.habitmemories.util.rowColors
import dev.sasikanth.colorsheet.ColorSheet
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var mHabitListRecyclerView: RecyclerView
    private lateinit var mHabitListAdapter: HabitListAdapter
    private lateinit var mMenu: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        checkFirstStart()

        initializeViews()
    }

    private fun checkFirstStart(): Boolean {
        val firstStart = App.sharedPreferences.getBoolean("firstStart", true)

        if (firstStart) {
            initializeDb()

            val editor = App.sharedPreferences.edit()
            editor.putBoolean("firstStart", false)

            editor.apply()
        }

        return firstStart
    }

    private fun initializeDb() {
        val ids = DBInterface.db.habitDao().insertAll(
            Habit(id = 0, "Reading Books", color = Color.GREEN, position = 0),
            Habit(id = 0, "Social Media", color = Color.RED, position = 1)
        )

        DBInterface.db.experienceDao().insertAll(
            Experience(
                id = 0,
                ids[0],
                "They make me wiser and happier",
                convertDrawableToByteArray(resources, R.drawable.book_ex1),
                Date().time,
                0
            ),
            Experience(
                id = 0,
                ids[0],
                "“We all get socialized once by our parents and teachers, ministers and priests. " +
                        "[Reading great books] is about getting a second chance. It’s not about being born " +
                        "again, but about growing up a second time, this time around as your own educator " +
                        "and guide, Virgil to yourself.”",
                convertDrawableToByteArray(resources, R.drawable.book_ex2),
                Date().time,
                1
            ),
            Experience(
                id = 0,
                ids[1],
                "Social media seriously harms your mental health",
                convertDrawableToByteArray(resources, R.drawable.book_ex3),
                Date().time,
                2
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        mMenu = menu!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_mode -> {
                val oldVal = mHabitListAdapter.isEditModeEnabled
                mHabitListAdapter.isEditModeEnabled = !oldVal

                mHabitListAdapter.editModeChange = true
                mHabitListAdapter.notifyDataSetChanged()
                mHabitListAdapter.editModeChange = false

                onEditModeButtonClicked(!oldVal)
                true
            }
            R.id.action_filter -> {
                ColorSheet().colorPicker(
                    colors = rowColors,
                    noColorOption = true,
                    listener = { color ->
                        AppSettings.habitListFilter = color.toString()
                        applyFilter()
                        onFilterChange()
                    })
                    .show(supportFragmentManager)
                true
            }
            R.id.clear_filters -> {
                onFilterChange(clear = true)
                AppSettings.habitListFilter = "clear"
                applyFilter()
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onFilterChange(clear: Boolean = false) {
        val view = mMenu.findItem(R.id.clear_filters)
        view.isVisible = !clear
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

                addDragDrop()

                applyFilter()
            }

        // fab
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val dialog = HabitDialog(this)
            dialog.setOnSubmit {
                val newHabit = Habit(0, it, position = mHabitListAdapter.habitList.size.toLong())
                HabitState.add(newHabit)
                mHabitListAdapter.addHabit(newHabit)
                mHabitListAdapter.notifyItemInserted(HabitState.lastIndex())
            }

            dialog.show()
        }
    }

    private fun applyFilter() {
        mHabitListAdapter.filter!!.filter(AppSettings.habitListFilter)
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

                    // filtreli listede swap et
                    Collections.swap(mHabitListAdapter.filteredHabits, from, to)

                    // asil listede swap et
                    val realFrom = mHabitListAdapter.habitList.indexOf(mHabitListAdapter.filteredHabits[from])
                    val realTo = mHabitListAdapter.habitList.indexOf(mHabitListAdapter.filteredHabits[to])

                    Collections.swap(mHabitListAdapter.habitList, realFrom, realTo)

                    // DB Pozisyonlari Swap Et
                    HabitState.swapPositions(mHabitListAdapter.habitList[realFrom], mHabitListAdapter.habitList[realTo])

                    recyclerView.adapter!!.notifyItemMoved(from, to)

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun isLongPressDragEnabled(): Boolean {
                    return false
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(mHabitListRecyclerView)
        mHabitListAdapter.mItemTouchHelper = itemTouchHelper
    }

    private fun onRowClicked(position: Int) {
        val rowHabit = mHabitListAdapter.filteredHabits[position]

        val intent = Intent(applicationContext, HabitDetailActivity::class.java)
        intent.putExtra("habitId", rowHabit.id)

        startActivity(intent)
    }

    private fun onColorPickerClicked(pos: Int, foreground: View) {
        val adapter = mHabitListAdapter
        val habit = mHabitListAdapter.filteredHabits[pos]

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
        val habit = mHabitListAdapter.filteredHabits[pos]

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
        val habit = mHabitListAdapter.filteredHabits[pos]

        HabitState.delete(habit)
        adapter.notifyItemRemoved(pos)
    }

    private fun onEditModeButtonClicked(isEditModeEnabled: Boolean) {
        val item: MenuItem = mMenu.findItem(R.id.action_edit_mode)
        if (isEditModeEnabled) {
            item.setIcon(R.drawable.ic_baseline_lock_open_white_24)
        } else {
            item.setIcon(R.drawable.ic_baseline_lock_white_24)
        }
    }



}