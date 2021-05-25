package com.tengri.habitmemories.state

import com.tengri.habitmemories.database.DBInterface
import com.tengri.habitmemories.database.daos.ExperienceDao
import com.tengri.habitmemories.database.entities.Experience

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
        return experiences
    }

    fun swapIds(ex1: Experience, ex2: Experience) {
        val id1 = ex1.id

        ex1.id = ex2.id
        ex2.id = id1

        experienceDao.update(ex1, ex2)
    }
}