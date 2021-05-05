package com.tengri.habitmemories.activity.habit_detail

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tengri.habitmemories.R
import com.tengri.habitmemories.activity.habit_detail.adapter.MemoryListAdapter
import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.database.entities.Memory
import com.tengri.habitmemories.dialogs.MemoryDialog
import com.tengri.habitmemories.state.MemoryState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var memoryListView: RecyclerView
    private var habitId: Long = -1
    private lateinit var habit: Habit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        habitId = intent.extras!!.getLong("habitId")
        habit = DBInterface.db.habitDao().findById(habitId)

        initializeViews(habitId)
    }

    private fun initializeViews(habitId: Long) {

        // toolbar
        val toolbar = supportActionBar
        toolbar!!.title = habit.name

        // recyclerview
        memoryListView = findViewById(R.id.memoryList)
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
                memoryListView.adapter = MemoryListAdapter(memories, onItemClicked = {
                    val memoryItem = memories[it]
                    Log.d("MEMORIES: ", memoryItem.content!!)
                }, onEditButtonClicked = { memory, pos, adapter ->
                    // open edit dialog
                    val dialog = MemoryDialog(this, memory.content!!)

                    dialog.setOnSubmit {
                        // update memory
                        memory.content = it

                        // update db
                        DBInterface.db.memoryDao().update(memory)

                        // update adapter
                        adapter.notifyItemChanged(pos)
                    }

                    dialog.show()
                }, onImageButtonClicked = { memory, pos, adapter ->

                    ImagePicker.with(this)
                        .compress(1024)
                        .start { resultCode, data ->
                            when (resultCode) {
                                Activity.RESULT_OK -> {
                                    val file: File = ImagePicker.getFile(data)!!

                                    memory.image = file.readBytes()

                                    adapter.notifyItemChanged(pos)

                                    DBInterface.db.memoryDao().update(memory)

                                }
                                ImagePicker.RESULT_ERROR -> {
                                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                })
            }

        // fab
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val dialog = MemoryDialog(this)
            dialog.setOnSubmit {
                MemoryState.addMemory(Memory(0, habitId, it, null))
                memoryListView.adapter!!.notifyItemInserted(MemoryState.lastIndex())
            }

            dialog.show()
        }
    }
}