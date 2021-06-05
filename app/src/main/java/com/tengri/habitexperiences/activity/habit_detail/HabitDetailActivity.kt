package com.tengri.habitexperiences.activity.habit_detail

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tengri.habitexperiences.App.Companion.sharedPreferences
import com.tengri.habitexperiences.R
import com.tengri.habitexperiences.activity.habit_detail.adapter.ExperienceListAdapter
import com.tengri.habitexperiences.activity.settings.SettingsActivity
import com.tengri.habitexperiences.database.DBInterface
import com.tengri.habitexperiences.database.entities.Habit
import com.tengri.habitexperiences.database.entities.Experience
import com.tengri.habitexperiences.dialogs.AboutDialog
import com.tengri.habitexperiences.dialogs.ExperienceDialog
import com.tengri.habitexperiences.state.ExperienceState
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.util.*

class HabitDetailActivity : AppCompatActivity() {

    private lateinit var mMenu: Menu
    private lateinit var experienceListAdapter: ExperienceListAdapter
    private lateinit var experienceListView: RecyclerView
    private var habitId: Long = -1
    private lateinit var habit: Habit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        habitId = intent.extras!!.getLong("habitId")
        habit = DBInterface.db.habitDao().findById(habitId)

        initializeViews(habitId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_habit_detail, menu)
        mMenu = menu!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit_mode -> {
                val oldVal = experienceListAdapter.isEditModeEnabled
                experienceListAdapter.isEditModeEnabled = !oldVal

                experienceListAdapter.editModeChange = true
                experienceListAdapter.notifyDataSetChanged()
                experienceListAdapter.editModeChange = false

                onEditModeButtonClicked(!oldVal)
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_about -> {
                AboutDialog(this).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onEditModeButtonClicked(any: Any) {
        TODO("Not yet implemented")
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

                addDragDrop()
            }

        // fab
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val dialog = ExperienceDialog(this)
            dialog.setOnSubmit {
                val nextItemPosition = experienceListAdapter.experienceList.size.toLong()

                ExperienceState.add(
                    Experience(
                        0,
                        habitId,
                        it,
                        null,
                        Date().time,
                        position = nextItemPosition
                    )
                )

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

                    ExperienceState.swapPositions(itemList[from], itemList[to])

                    Collections.swap(itemList, from, to)

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
        itemTouchHelper.attachToRecyclerView(experienceListView)
        experienceListAdapter.mItemTouchHelper = itemTouchHelper
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