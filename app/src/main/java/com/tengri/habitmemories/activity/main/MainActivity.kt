package com.tengri.habitmemories.activity.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tengri.habitmemories.R
import com.tengri.habitmemories.activity.habit_detail.HabitDetailActivity
import com.tengri.habitmemories.activity.main.adapter.HabitListAdapter
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.state.HabitState
import com.tengri.uiexamples.HabitAddDialog
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    private lateinit var habitListRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

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
                habitListRecyclerView.adapter = HabitListAdapter(habits as MutableList<Habit>, onItemClicked = {
                    val rowHabit = habits[it]

                    val intent = Intent(this, HabitDetailActivity::class.java)
                    intent.putExtra("habitId", rowHabit.id)

                    startActivity(intent)
                })
            }

        // fab
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val habitAddDialog = HabitAddDialog(this)
            habitAddDialog.setOnSubmit {
                HabitState.addHabit(Habit(0, it))
                habitListRecyclerView.adapter!!.notifyItemInserted(HabitState.lastIndex())
            }

            habitAddDialog.show()
        }
    }

}