package com.tengri.habitmemories.activity.habit_detail

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tengri.habitmemories.App.Companion.sharedPreferences
import com.tengri.habitmemories.R
import com.tengri.habitmemories.activity.habit_detail.adapter.ExperienceListAdapter
import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.entities.Habit
import com.tengri.habitmemories.database.entities.Experience
import com.tengri.habitmemories.dialogs.ExperienceDialog
import com.tengri.habitmemories.state.ExperienceState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.util.*

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var experienceListAdapter: ExperienceListAdapter
    private lateinit var experienceListView: RecyclerView
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
        toolbar!!.title = "${habit.name}"

        // recyclerview
        experienceListView = findViewById(R.id.experienceList)
        Observable.fromCallable {
            return@fromCallable ExperienceState.get(habitId)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { experiences ->
                experienceListView.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                experienceListAdapter = ExperienceListAdapter(experiences, onItemClicked = {
                    val item = experiences[it]
                    Log.d("Experiences: ", item.content!!)
                }, onEditButtonClicked = { experience, pos, adapter ->
                    // open edit dialog
                    val dialog = ExperienceDialog(this, experience.content!!)

                    dialog.setOnSubmit {
                        // update experience
                        experience.content = it

                        // update db
                        DBInterface.db.experienceDao().update(experience)

                        // update adapter
                        adapter.notifyItemChanged(pos)
                    }

                    dialog.show()
                }, onImageButtonClicked = { experience, pos, adapter ->

                    val compressLevel = sharedPreferences.getString("compress_level", "1")!!.toInt()

                    ImagePicker.with(this)
                        .compress(compressLevel * 1024)
                        .start { resultCode, data ->
                            when (resultCode) {
                                Activity.RESULT_OK -> {
                                    val file: File = ImagePicker.getFile(data)!!

                                    experience.image = file.readBytes()

                                    adapter.notifyItemChanged(pos)

                                    DBInterface.db.experienceDao().update(experience)

                                }
                                ImagePicker.RESULT_ERROR -> {
                                    Toast.makeText(
                                        this,
                                        ImagePicker.getError(data),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                else -> {
                                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }

                })
                experienceListView.adapter = experienceListAdapter
            }

        addDragDrop()

        // fab
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val dialog = ExperienceDialog(this)
            dialog.setOnSubmit {
                ExperienceState.add(Experience(0, habitId, it, null, Date().time))
                experienceListView.adapter!!.notifyItemInserted(ExperienceState.lastIndex())
            }

            dialog.show()
        }
    }

    private fun addDragDrop() {
        val simpleCallback =
            object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition

                    val itemList = experienceListAdapter.experienceList

                    ExperienceState.swapIds(itemList[from], itemList[to])

                    Collections.swap(itemList, from, to)

                    recyclerView.adapter!!.notifyItemMoved(from, to)

                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

            }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(experienceListView)
    }
}