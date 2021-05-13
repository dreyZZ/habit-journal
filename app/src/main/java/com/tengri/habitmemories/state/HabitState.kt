package com.tengri.habitmemories.state

import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.daos.HabitDao
import com.tengri.habitmemories.database.entities.Habit

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

    fun addHabit(habit: Habit) {
        // TODO: 24/04/2021 block ediyor olabilir
        val ids = habitDao.insertAll(habit)

        habit.id = ids[0]
        habit.position = ids[0]

        habitDao.update(habit)

        habits.add(habit)
    }

    fun deleteHabit(habit: Habit) {
        habitDao.delete(habit)

        habits.remove(habit)
    }

    fun lastIndex(): Int {
        return habits.size - 1
    }

    fun swapIds(from: Int, to: Int) {
        val habit1 = habits[from]
        val habit2 = habits[to]

        val pos1 = habit1.position

        habit1.position = habit2.position
        habit2.position = pos1

        habitDao.update(habit1, habit2)
    }
}