package com.heroslender.hmf.intellij.preview.components

import com.heroslender.hmf.intellij.preview.invokePreview
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent
import com.intellij.task.ProjectTaskManager
import com.intellij.ui.AncestorListenerAdapter
import com.intellij.util.messages.MessageBusConnection
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.AncestorEvent

class MenuPreviewComponent(
    private val myProject: Project,
    private val myFunction: KtNamedFunction,
) : JLayeredPane() {
    private val rebuildTask = RebuildTask(ProjectTaskManager.getInstance(myProject)) {
        reCompose()
        redraw()
    }

    private var menusPanel = MenuListComponent(this, MenuListComponent.Options())
    private var messageBus: MessageBusConnection? = null

    init {
        layout = LayeredPaneLayout(this)
        isVisible = true
        minimumSize = Dimension(
            50,
            50
        )

        addMouseWheelListener {
            // Just to fix an issue that would cause the resized components to reset
        }

        addAncestorListener(object : AncestorListenerAdapter() {
            override fun ancestorMoved(event: AncestorEvent) {
                redraw()
            }
        })

        setup()
    }

    private var component: MenuComponent? = null

    private fun reCompose() {
        component = invokePreview(myFunction)
    }

    fun dispose() {
        messageBus?.disconnect()
        messageBus = null
    }

    fun setup() {
        reCompose()
        redraw()

        if (messageBus == null) {
            this.messageBus = myProject.messageBus.connect().apply {
                subscribe(VirtualFileManager.VFS_CHANGES, object : BulkFileListener {
                    override fun after(events: List<VFileEvent?>) {
                        rebuildTask.run()
                    }
                })
            }
        }
    }

    private fun redraw() {
        val myUi = this
        if (!myUi.isShowing) return

        // clear the old UI
        myUi.removeAll()

        val component = this.component
        if (component == null) {
            myUi.add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                border = EmptyBorder(10, 10, 0, 0) // Margin around the element

                add(
                    JLabel("Component wasn't rendered?!").bindToLeft().apply {
                        border = EmptyBorder(0, 0, 5, 0)
                    },
                    BorderLayout.CENTER
                )

                add(JButton("Rebuild").apply {
                    addActionListener {
                        rebuildTask.run()
                    }
                })
            })
            return
        }

        val opts = menusPanel.opts
        menusPanel = MenuListComponent(myUi, opts).apply {
            add(
                JPanel().apply {
                    layout = BoxLayout(this, BoxLayout.LINE_AXIS)
                    border = EmptyBorder(40, 0, 10, 0) // Margin around the element

                    add(JLabel(myFunction.name + " has @Preview! (${myFunction.fqName?.asString()})"))
                }
            )

            add(component)
        }

        val toolbar = ToolbarComponent().apply {
            addButton(AllIcons.General.Add) {
                menusPanel.opts.zoomFactor *= 1.1
                myUi.repaint()
            }
            addButton(AllIcons.General.Remove) {
                menusPanel.opts.zoomFactor /= 1.1
                myUi.repaint()
            }
            addButton(AllIcons.General.ActualZoom) {
                menusPanel.opts.apply {
                    zoomFactor = 1.0
                    xOffset = 0.0
                    yOffset = 0.0
                    xDiff = 0
                    yDiff = 0
                    dragger = true
                    released = true
                }
                myUi.repaint()
            }
            addButton(AllIcons.Actions.Restart) {
                rebuildTask.run()
            }
        }

        val panel = JPanel().apply {
            layout = FlowLayout(FlowLayout.CENTER, 0, 0)
            add(toolbar)
            setBounds(
                myUi.width - (this.preferredSize.width + 20),
                myUi.height - (this.preferredSize.height + 50),
                this.preferredSize.width,
                this.preferredSize.height
            )
        }

        myUi.setLayer(menusPanel, 0)
        myUi.add(menusPanel)

        myUi.setLayer(panel, 1)
        myUi.add(panel)

        myUi.validate()
    }

    private fun JComponent.bindToLeft(): JPanel {
        return JPanel().apply {
            isVisible = true
            layout = BoxLayout(this, BoxLayout.X_AXIS)

            add(this@bindToLeft)
        }
    }

    private class RebuildTask(
        private val projectTaskManager: ProjectTaskManager,
        private val onRebuild: () -> Unit,
    ) : Runnable {
        private var inProgress = false
        private var shouldRebuild = false

        private var lastRebuild = System.currentTimeMillis()

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
            projectTaskManager.buildAllModules().onSuccess {
                onRebuild()

                inProgress = false
                if (shouldRebuild) {
                    shouldRebuild = false
                    run()
                }
            }

            onRebuild()
        }
    }
}