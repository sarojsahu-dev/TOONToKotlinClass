package com.toon.kotlin.toontokotlinclass.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.toon.kotlin.toontokotlinclass.ui.ToonGenerateDialog

class GenerateToonKotlinAction : AnAction("Generate Kotlin From TOON") {

    override fun actionPerformed(e: AnActionEvent) {
        val dialog = ToonGenerateDialog()

        if (dialog.showAndGet()) {
            val toonText = dialog.getToonInput()
            dialog.setPreview("Parsed TOON:\n\n$toonText")
        }
    }
}
