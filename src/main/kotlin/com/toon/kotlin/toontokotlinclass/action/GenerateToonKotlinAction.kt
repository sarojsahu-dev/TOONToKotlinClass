package com.toon.kotlin.toontokotlinclass.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.toon.kotlin.toontokotlinclass.generator.KotlinClassGenerator
import com.toon.kotlin.toontokotlinclass.parser.ToonParser
import com.toon.kotlin.toontokotlinclass.ui.ToonGenerateDialog
import com.toon.kotlin.toontokotlinclass.writer.PsiKotlinWriter

/**
 * IntelliJ action that opens the TOON to Kotlin generator dialog.
 *
 * This action is registered in plugin.xml and appears in the "Generate" menu.
 * It orchestrates the parsing, generation, and file writing workflow.
 */
class GenerateToonKotlinAction : AnAction("Generate Kotlin From TOON") {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return

        val dialog = ToonGenerateDialog()

        dialog.onUpdatePreview = { toon ->
            try {
                if (toon.isBlank()) {
                    emptyMap()
                } else {
                    val parser = ToonParser()
                    val ast = parser.parse(toon)

                    // Pass the full advanced settings object to the generator
                    val generator = KotlinClassGenerator(
                        settings = dialog.getAdvancedSettings(),
                        packageName = dialog.getPackageName()
                    )

                    generator.generateAll(ast)
                }
            } catch (e: Exception) {
                throw e
            }
        }

        dialog.onGenerateClicked = { files ->
            try {
                PsiKotlinWriter.writeMultipleFiles(
                    project = project,
                    files = files,
                    packageName = dialog.getPackageName()
                )
            } catch (e: Exception) {
                com.intellij.openapi.ui.Messages.showErrorDialog(
                    project,
                    "Failed to generate files: ${e.message}",
                    "Generation Error"
                )
            }
        }

        dialog.show()
    }
}