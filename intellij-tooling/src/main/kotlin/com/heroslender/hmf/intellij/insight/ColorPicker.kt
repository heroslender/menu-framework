package com.heroslender.hmf.intellij.insight

import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.ColorIcon
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class ColorPicker(parent: JComponent) {

    private val panel = JPanel(GridBagLayout())

    private var chosenColor: String? = null
    private val dialog: ColorPickerDialog

    init {
        dialog = ColorPickerDialog(parent, panel)
    }

    fun showDialog(): String? {
        init()

        dialog.show()

        return chosenColor
    }

    private fun init() {
        val iterator = Color.values().iterator()
        val size = Color.values().size
        for (i in 0..size / 15)
            addToPanel(i, 15, panel, iterator)
    }

    private fun addToPanel(
        row: Int,
        cols: Int,
        panel: JPanel,
        iterator: Iterator<Color>,
    ) {
        for (i in 0 until cols) {
            if (!iterator.hasNext()) {
                break
            }

            val entry = iterator.next()
            val icon = ColorIcon(20, entry.color, true)

            val label = JLabel(icon)
            label.addMouseListener(
                object : MouseAdapter() {
                    override fun mouseClicked(e: MouseEvent?) {
                        chosenColor = entry.name
                        dialog.close(0)
                    }
                }
            )

            val constraints = GridBagConstraints()
            constraints.gridy = row
            constraints.fill = GridBagConstraints.NONE
            constraints.insets = Insets(2, 2, 2, 2)

            panel.add(label, constraints)
        }
    }

    private class ColorPickerDialog constructor(parent: JComponent, private val component: JComponent) :
        DialogWrapper(parent, false) {

        init {
            title = "Choose Color"
            isResizable = true

            init()
        }

        override fun createCenterPanel(): JComponent {
            return component
        }
    }
}