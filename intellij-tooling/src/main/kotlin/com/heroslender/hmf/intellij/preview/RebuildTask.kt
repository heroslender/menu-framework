package com.heroslender.hmf.intellij.preview

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.task.ProjectTaskManager
import com.intellij.util.messages.MessageBusConnection

class RebuildTask(
    project: Project,
) : Runnable {
    private val messageBus: MessageBusConnection
    private val projectTaskManager: ProjectTaskManager = ProjectTaskManager.getInstance(project)

    var inProgress = false
        private set
    private var shouldRebuild = false

    private var lastRebuild = System.currentTimeMillis()

    private val onRebuildStart: MutableList<() -> Unit> = mutableListOf()
    private val onRebuildFinish: MutableList<() -> Unit> = mutableListOf()

    init {
        this.messageBus = project.messageBus.connect().apply {
            subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
                override fun after(events: List<VFileEvent?>) {
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

    override fun run() {
        val now = System.currentTimeMillis()
        if (now - lastRebuild < 100) {
            return
        }
        lastRebuild = now

        if (inProgress) {
            shouldRebuild = true
            return
        }

        inProgress = true
        onRebuildStart.forEach { it() }
        projectTaskManager.buildAllModules().onSuccess {
            onRebuildFinish.forEach { it() }

            inProgress = false
            if (shouldRebuild) {
                shouldRebuild = false
                run()
            }
        }
    }
}