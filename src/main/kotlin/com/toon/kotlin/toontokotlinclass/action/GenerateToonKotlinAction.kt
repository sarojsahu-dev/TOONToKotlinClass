package com.toon.kotlin.toontokotlinclass.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.toon.kotlin.toontokotlinclass.generator.KotlinClassGenerator
import com.toon.kotlin.toontokotlinclass.parser.ToonParser
import com.toon.kotlin.toontokotlinclass.ui.ToonGenerateDialog

class GenerateToonKotlinAction : AnAction("Generate Kotlin From TOON") {

    override fun actionPerformed(e: AnActionEvent) {

        val dialog = ToonGenerateDialog()

        // Show UI dialog
        if (!dialog.showAndGet()) return

        // 1. Read the TOON input
        val toonText = dialog.getToonInput()

        // 2. Parse TOON to AST
        val parser = ToonParser()
        val nodes = parser.parse(toonText)

        // 3. Generate Kotlin from AST
        val generator = KotlinClassGenerator(
            useVal = dialog.isUseVal(),
            nullable = dialog.isUseNullable(),
            framework = dialog.getAnnotationFramework()
        )

        val kotlinCode = generator.generateClass(
            rootName = "Root",
            nodes = nodes
        )

        // 4. Show it in the Kotlin preview box
        dialog.setPreview(kotlinCode)
    }
}