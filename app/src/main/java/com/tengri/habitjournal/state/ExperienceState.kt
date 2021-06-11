package com.tengri.habitjournal.state

import com.tengri.habitjournal.database.DBInterface
import com.tengri.habitjournal.database.daos.ExperienceDao
import com.tengri.habitjournal.database.entities.Experience

object ExperienceState {
    private var experienceDao: ExperienceDao = DBInterface.db.experienceDao()
    lateinit var experiences: MutableList<Experience>

    init {
    }

    fun add(experience: Experience) {
        // TODO: 24/04/2021 block ediyor olabilir
        val ids = experienceDao.insertAll(experience)

        experience.id = ids[0]

        experiences.add(experience)
    }

    fun delete(experience: Experience) {
        experienceDao.delete(experience)

        experiences.remove(experience)
    }

    fun lastIndex(): Int {
        return experiences.size - 1
    }

    fun get(habitId: Long): MutableList<Experience> {
        experiences = experienceDao.getByHabitId(habitId) as MutableList<Experience>
        experiences.sortBy { it.position }
        return experiences
    }

    fun swapPositions(ex1: Experience, ex2: Experience) {
        val pos1 = ex1.position

        ex1.position = ex2.position
        ex2.position = pos1

        experienceDao.update(ex1, ex2)
    }
}