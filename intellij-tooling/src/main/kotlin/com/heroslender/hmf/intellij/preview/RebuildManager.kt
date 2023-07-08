package com.heroslender.hmf.intellij.preview

import com.intellij.openapi.project.Project

object RebuildManager {
    private val tasks = mutableMapOf<Project, RebuildTask>()

    fun getOrCreateTask(project: Project): RebuildTask {
        return tasks.getOrPut(project) { RebuildTask(project) }
    }

    fun getOrNullTask(project: Project): RebuildTask? {
        return tasks[project]
    }
}