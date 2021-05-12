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
        habits.sortBy { it.id }
        return habits
    }

    fun addHabit(habit: Habit) {
        // TODO: 24/04/2021 block ediyor olabilir
        val ids = habitDao.insertAll(habit)

        habit.id = ids[0].toLong()

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

        val id1 = habit1.id

        habit1.id = habit2.id
        habit2.id = id1

        habitDao.update(habit1, habit2)
    }
}