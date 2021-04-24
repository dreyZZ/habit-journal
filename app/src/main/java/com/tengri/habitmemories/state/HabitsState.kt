package com.tengri.habitmemories.state

import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.daos.HabitDao
import com.tengri.habitmemories.database.entities.Habit

object HabitsState {
    private var habitDao: HabitDao = DBInterface.db.habitDao()
    lateinit var habits: MutableList<Habit>

    init {
    }

    fun getDBHabits(): MutableList<Habit> {
        habits = habitDao.getAll() as MutableList<Habit>
        return habits
    }

    fun addHabit(habit: Habit) {
        // TODO: 24/04/2021 block ediyor olabilir
        val ids = habitDao.insertAll(habit)

        habit.uid = ids[0].toInt()

        habits.add(habit)
    }

    fun deleteHabit(habit: Habit) {
        habitDao.delete(habit)

        habits.remove(habit)
    }

    fun lastIndex(): Int {
        return habits.size - 1
    }
}