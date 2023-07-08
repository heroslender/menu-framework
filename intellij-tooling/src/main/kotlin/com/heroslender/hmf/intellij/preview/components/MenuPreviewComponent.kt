package com.heroslender.hmf.intellij.preview.components

import com.heroslender.hmf.intellij.preview.RebuildManager
import com.heroslender.hmf.intellij.preview.impl.PreviewMenu
import com.intellij.execution.runners.ExecutionUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.Content
import com.intellij.util.ui.JBUI
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.containingClass
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class MenuPreviewComponent(
    val menuPreviewId: String,
    private val myProject: Project,
    private val myFunction: KtNamedFunction,
    private val toolWindow: ToolWindow,
) : JLayeredPane(), ToolbarListener {
    lateinit var content: Content
    val rebuildTask = RebuildManager.getOrCreateTask(myProject)

    private val menu = PreviewMenu(myFunction)
    private var menuCanvas = MenuCanvasComponent(this, MenuComponent(menu), MenuCanvasComponent.Options())
    private val toolbar = ToolbarComponent(this).apply {
        setBounds(
            width - (this.preferredSize.width + 20),
            height - (this.preferredSize.height + 50),
            this.preferredSize.width,
            this.preferredSize.height
        )
    }

    companion object {
        val PreviewIcon = AllIcons.General.LayoutPreviewOnly
        val PreviewBuildingIcon = ExecutionUtil.getLiveIndicator(PreviewIcon)
    }

    init {
        layout = MenuPreviewLayout(this)
        isVisible = true
        minimumSize = Dimension(
            150,
            50
        )

        addMouseWheelListener {
            // Just to fix an issue that would cause the resized components to reset
        }

        with(rebuildTask) {
            listenStart {
                content.icon = PreviewBuildingIcon
            }

            listen {
                content.icon = PreviewIcon
                reCompose()
            }
        }

        menuCanvas.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                toolWindow.activate(null)
            }
        })

        reCompose()
        redraw()
    }

    private var errorMsg: String? = "null"

    private fun reCompose() {
        val hadError = errorMsg != null
        try {
            errorMsg = null

            menuCanvas.menuComponent.update()
            if (this::content.isInitialized) {
                content.displayName = menu.name
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
        } catch (e: Exception) {
            errorMsg = "Failed to start: ${e.message}"
            e.printStackTrace()
        } finally {
            if (hadError != (errorMsg != null)) {
                redraw()
            } else {
                repaint()
            }
        }
    }

    private fun redraw() {
        val myUi = this
        if (!myUi.isShowing) return

        // clear the old UI
        myUi.removeAll()

        if (errorMsg != null) {
            myUi.add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
                border = JBUI.Borders.empty(10, 10, 0, 0) // Margin around the element

                add(
                    JLabel("Component wasn't rendered?!").bindToLeft().apply {
                        border = JBUI.Borders.emptyBottom(5)
                    },
                    BorderLayout.CENTER
                )

                if (errorMsg != null) {
                    add(
                        JLabel(errorMsg).bindToLeft().apply {
                            border = JBUI.Borders.emptyBottom(5)
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

        myUi.add(menuCanvas, 0, -1)
        myUi.add(toolbar, 1, -1)
        myUi.validate()
    }

    override fun onZoomIn() {
        menuCanvas.opts.zoomFactor *= 1.1
        repaint()
    }

    override fun onZoomOut() {
        menuCanvas.opts.zoomFactor /= 1.1
        repaint()
    }

    override fun onResetZoom() {
        menuCanvas.opts.apply {
            zoomFactor = 1.0
            xOffset = 0.0
            yOffset = 0.0
            xDiff = 0
            yDiff = 0
            dragger = true
            released = true
        }
        repaint()
    }

    override fun onRebuild() {
        rebuildTask.run()
    }

    private fun JComponent.bindToLeft(): JPanel {
        return JPanel().apply {
            isVisible = true
            layout = BoxLayout(this, BoxLayout.X_AXIS)

            add(this@bindToLeft)
        }
    }
}