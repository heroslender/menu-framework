package com.heroslender.hmf.intellij.preview.components

import com.intellij.openapi.actionSystem.ActionButtonComponent
import com.intellij.openapi.actionSystem.ex.ActionButtonLook
import com.intellij.openapi.keymap.impl.IdeMouseEventDispatcher
import com.intellij.ui.ExperimentalUI
import com.intellij.util.ui.JBInsets
import com.intellij.util.ui.StartupUiUtil
import java.awt.AWTEvent
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Insets
import java.awt.event.MouseEvent
import javax.swing.Icon
import javax.swing.JComponent

class ToolbarButton(
    private val icon: Icon,
    private val onClick: () -> Unit,
) : JComponent(), ActionButtonComponent {
    private var myInsets: Insets = JBInsets.emptyInsets()

    init {
        enableEvents(AWTEvent.MOUSE_EVENT_MASK)

        setIconInsets(JBInsets.create(5, 5))
    }

    fun setIconInsets(insets: Insets?) {
        myInsets = if (insets != null) JBInsets.create(insets) else JBInsets.emptyInsets()
    }

    override fun paintComponent(g: Graphics) {
        val look: ActionButtonLook = ActionButtonLook.SYSTEM_LOOK
        if (isEnabled || !StartupUiUtil.isUnderDarcula() || ExperimentalUI.isNewUI()) {
            look.paintBackground(g, this)
        }
        look.paintIcon(g, this, icon)
        look.paintBorder(g, this)
    }

    override fun getPreferredSize(): Dimension {
        val size = Dimension(icon.iconWidth, icon.iconHeight)
        JBInsets.addTo(size, myInsets)
        JBInsets.addTo(size, insets)

        return size
    }

    override fun getMinimumSize(): Dimension {
        return preferredSize
    }


    override fun processMouseEvent(e: MouseEvent) {
        IdeMouseEventDispatcher.requestFocusInNonFocusedWindow(e)
        super.processMouseEvent(e)
        if (e.isConsumed) return
        when (e.id) {
            MouseEvent.MOUSE_PRESSED -> {
                popstate = ActionButtonComponent.PUSHED
                repaint()
            }

            MouseEvent.MOUSE_RELEASED -> {
                popstate = ActionButtonComponent.POPPED
                onClick()
                repaint()
            }

            MouseEvent.MOUSE_ENTERED -> {
                popstate = ActionButtonComponent.POPPED
                repaint()
            }

            MouseEvent.MOUSE_EXITED -> {
                popstate = ActionButtonComponent.NORMAL
                repaint()
            }
        }
    }

    var popstate: Int = ActionButtonComponent.NORMAL

    override fun getPopState(): Int {
        return this.popstate
    }
}
