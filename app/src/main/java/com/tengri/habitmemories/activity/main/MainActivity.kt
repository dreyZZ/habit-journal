package com.tengri.habitmemories.activity.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tengri.habitmemories.R
import com.tengri.habitmemories.activity.main.adapter.HabitListAdapter
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.state.HabitsState
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
            return@fromCallable HabitsState.getDBHabits()
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { habits ->
                habitListRecyclerView.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                habitListRecyclerView.adapter = HabitListAdapter(habits as MutableList<Habit>)
            }

        // fab
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            HabitsState.addHabit(Habit(0, "Deneme"))
            habitListRecyclerView.adapter!!.notifyItemInserted(HabitsState.lastIndex())
        }
    }

}