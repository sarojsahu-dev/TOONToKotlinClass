package com.toon.kotlin.toontokotlinclass.writer

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile

object PsiKotlinWriter {

    fun writeMultipleFiles(
        project: Project, 
        files: Map<String, String>,
        packageName: String = ""
    ) {
        
        try {
            WriteCommandAction.runWriteCommandAction(project) {

                val root = project.baseDir ?: throw IllegalStateException("Project base directory not found")

                // Create package structure if specified
                val targetFolder = if (packageName.isNotEmpty()) {
                    createPackageStructure(root, packageName)
                } else {
                    // Create /generated/ folder if missing
                    root.findChild("generated") ?: root.createChildDirectory(this, "generated")
                }

                val openedFiles = mutableListOf<VirtualFile>()

                files.forEach { (fileName, content) ->
                    
                    // Add package declaration if package name is specified
                    val finalContent = if (packageName.isNotEmpty() && !content.startsWith("package")) {
                        "package $packageName\n\n$content"
                    } else {
                        content
                    }

                    val existing = targetFolder.findChild(fileName)
                    existing?.delete(this)

                    val vf = targetFolder.createChildData(this, fileName)
                    VfsUtil.saveText(vf, finalContent)

                    openedFiles += vf
                }

                // Open all written files
                val editor = FileEditorManager.getInstance(project)
                openedFiles.forEach { editor.openFile(it, true) }
                
                // Show success notification
                showSuccessNotification(project, files.size, targetFolder.path)
            }
        } catch (e: Exception) {
            showErrorNotification(project, e.message ?: "Unknown error")
            throw e
        }
    }
    
    private fun createPackageStructure(root: VirtualFile, packageName: String): VirtualFile {
        val parts = packageName.split(".")
        var current = root.findChild("src")?.findChild("main")?.findChild("kotlin")
            ?: root.findChild("generated")
            ?: root.createChildDirectory(this, "generated")
        
        parts.forEach { part ->
            current = current.findChild(part) ?: current.createChildDirectory(this, part)
        }
        
        return current
    }
    
    private fun showSuccessNotification(project: Project, fileCount: Int, path: String) {
        try {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("TOON Generator")
                .createNotification(
                    "Kotlin Files Generated",
                    "Successfully generated $fileCount file(s) at: $path",
                    NotificationType.INFORMATION
                )
                .notify(project)
        } catch (e: Exception) {
            // Fallback if notification group not found
            println("Generated $fileCount files at $path")
        }
    }
    
    private fun showErrorNotification(project: Project, message: String) {
        try {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("TOON Generator")
                .createNotification(
                    "Generation Failed",
                    message,
                    NotificationType.ERROR
                )
                .notify(project)
        } catch (e: Exception) {
            println("Error: $message")
        }
    }
}