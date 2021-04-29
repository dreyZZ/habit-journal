package com.tengri.habitmemories.activity.habit_detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tengri.habitmemories.R
import com.tengri.habitmemories.database.entities.Memory
import com.tengri.habitmemories.state.MemoryState
import com.tengri.habitmemories.activity.habit_detail.adapter.MemoryListAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var memoryListView: RecyclerView
    private var habitId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)

        habitId = intent.extras!!.getLong("habitId")

        initializeViews(habitId)
    }

    private fun initializeViews(habitId: Long) {
        memoryListView = findViewById(R.id.memoryList)

        // recyclerview
        Observable.fromCallable {
            return@fromCallable MemoryState.getMemories(habitId)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { memories ->
                memoryListView.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                memoryListView.adapter = MemoryListAdapter(memories as MutableList<Memory>, onItemClicked = {
                    val memoryItem = memories[it]
                    Log.d("MEMORIES: ", memoryItem.content!!)
                })
            }
    }
}