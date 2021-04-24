package com.tengri.habitmemories.activity.main

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tengri.habitmemories.R
import com.tengri.habitmemories.activity.main.adapter.HabitListAdapter
import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.daos.HabitDao
import com.tengri.habitmemories.database.entities.Habit
import executeAsyncTask
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var habits: List<Habit>
    private lateinit var habitDao: HabitDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        habitDao = DBInterface.db.habitDao()

        Executors.newSingleThreadExecutor().execute {
            habits = habitDao.getAll()

            Handler(Looper.getMainLooper()).post{
                val basicRecyclerView = findViewById<RecyclerView>(R.id.habitList)
                basicRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                basicRecyclerView.adapter = HabitListAdapter(habits as MutableList<Habit>)
            }
        }

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            // Add a habit
        }

    }
}