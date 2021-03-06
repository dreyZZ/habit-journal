package com.tengri.habitjournal.state

import com.tengri.habitjournal.database.DBInterface
import com.tengri.habitjournal.database.daos.HabitDao
import com.tengri.habitjournal.database.entities.Habit

object HabitState {
    private var habitDao: HabitDao = DBInterface.db.habitDao()
    lateinit var habits: MutableList<Habit>

    init {
    }

    fun getDBHabits(): MutableList<Habit> {
        habits = habitDao.getAll().toMutableList()
        habits.sortBy { it.position }
        return habits
    }

    fun add(habit: Habit): Habit {
        // TODO: 24/04/2021 block ediyor olabilir
        val ids = habitDao.insertAll(habit)

        habit.id = ids[0]
        habit.position = ids[0]

        habitDao.update(habit)

        return habit
    }

    fun delete(habit: Habit) {
        habitDao.delete(habit)

        habits.remove(habit)
    }

    fun lastIndex(): Int {
        return habits.size - 1
    }

    fun swapPositions(habit1: Habit, habit2: Habit) {
        val pos1 = habit1.position

        habit1.position = habit2.position
        habit2.position = pos1

        habitDao.update(habit1, habit2)
    }
}