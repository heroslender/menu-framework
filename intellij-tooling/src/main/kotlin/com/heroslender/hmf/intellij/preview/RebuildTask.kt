package com.heroslender.hmf.intellij.preview

import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.task.ProjectTaskManager
import com.intellij.util.messages.MessageBusConnection

class RebuildTask(
    private val myProject: Project,
) {
    private val messageBus: MessageBusConnection
    private val projectTaskManager: ProjectTaskManager = ProjectTaskManager.getInstance(myProject)

    var inProgress = false
        private set
    private var shouldRebuild = false

    private var lastRebuild = System.currentTimeMillis()

    private val onRebuildStart: MutableList<() -> Unit> = mutableListOf()
    private val onRebuildFinish: MutableList<() -> Unit> = mutableListOf()

    init {
        this.messageBus = myProject.messageBus.connect().apply {
            subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
                override fun after(events: List<VFileEvent>) {
                    run()
                }
            })
        }
    }

    fun listen(onRebuild: () -> Unit) {
        this.onRebuildFinish.add(onRebuild)
    }

    fun listenStart(onStart: () -> Unit) {
        onRebuildStart.add(onStart)
    }

    fun run(reschedule: Boolean = true) {
        val now = System.currentTimeMillis()
        if (now - lastRebuild < 100) {
            return
        }
        lastRebuild = now

        if (inProgress) {
            shouldRebuild = reschedule
            return
        }

        inProgress = true
        onRebuildStart.forEach { it() }
        val buildTask = projectTaskManager.createModulesBuildTask(
            /* modules = */ ModuleManager.getInstance(myProject).modules,
            /* isIncrementalBuild = */ true,
            /* includeDependentModules = */ false,
            /* includeRuntimeDependencies = */ false,
            /* includeTests = */ false
        )
        projectTaskManager
            .run(buildTask)
            .onError {
                inProgress = false
                shouldRebuild = false
            }.onSuccess {
                inProgress = false

                onRebuildFinish.forEach { it() }

                if (shouldRebuild) {
                    shouldRebuild = false
                    run()
                }
            }
    }
}