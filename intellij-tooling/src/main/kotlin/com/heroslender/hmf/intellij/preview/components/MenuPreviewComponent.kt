package com.heroslender.hmf.intellij.preview.components

import com.heroslender.hmf.intellij.preview.RebuildManager
import com.heroslender.hmf.intellij.preview.invokePreview
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.AncestorListenerAdapter
import com.intellij.ui.content.Content
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.event.AncestorEvent

class MenuPreviewComponent(
    val menuPreviewId: String,
    private val myProject: Project,
    private val myFunction: KtNamedFunction,
    private val toolWindow: ToolWindow,
) : JLayeredPane() {
    lateinit var content: Content
    val rebuildTask = RebuildManager.getOrCreateTask(myProject)

    private var menusPanel = MenuListComponent(this, MenuListComponent.Options())

    companion object {
        val PreviewIcon = AllIcons.General.LayoutPreviewOnly
        val PreviewBuildingIcon = ExecutionUtil.getLiveIndicator(PreviewIcon)
    }

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

        setup()

        rebuildTask.listenStart {
            content.icon = PreviewBuildingIcon
        }

        rebuildTask.listen {
            content.icon = PreviewIcon
            reCompose()
            redraw()
        }

        addAncestorListener(object : AncestorListenerAdapter() {
            override fun ancestorMoved(event: AncestorEvent) {
                redraw()
            }
        })
    }

    var menuComponent: MenuComponent? = null
    private var errorMsg: String? = null

    private fun reCompose() {
        try {
            errorMsg = null
            menuComponent = invokePreview(myFunction)
            menuComponent?.menuName?.also {
                if (this::content.isInitialized) {
                    content.displayName = it
                }
            }
        } catch (e: ClassNotFoundException) {
            val functionParent = myFunction.parent
            val className: String = if (functionParent is KtFile) {
                functionParent.packageFqName.asString() + '.' + functionParent.name
            } else {
                myFunction.containingClass()?.fqName?.asString() ?: "Unknown"
            }

            errorMsg = "Class not found: $className"
        } catch (e: NoSuchMethodException) {
            errorMsg = "Method not found: ${myFunction.name}"
        }
    }

    fun setup() {
        reCompose()
        redraw()
    }

    private fun redraw() {
        val myUi = this
        if (!myUi.isShowing) return

        // clear the old UI
        myUi.removeAll()

        val component = this.menuComponent
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

                if (errorMsg != null) {
                    add(
                        JLabel(errorMsg).bindToLeft().apply {
                            border = EmptyBorder(0, 0, 5, 0)
                        },
                        BorderLayout.CENTER
                    )
                }

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

            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    toolWindow.activate(null)
                }
            })
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
}