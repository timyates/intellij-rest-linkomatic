/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.bloidonia.intellij.linkomatic

import com.intellij.ide.actions.CopyReferenceAction.VirtualFileQualifiedNameProvider
import com.intellij.ide.scratch.RootType
import com.intellij.ide.scratch.ScratchFileService
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import java.awt.datatransfer.StringSelection
import java.io.File

/**
 *
 * A lot of this was taken from:
 *
 * https://github.com/JetBrains/intellij-community/blob/master/platform/lang-impl/src/com/intellij/ide/actions/CopyReferenceAction.java
 *
 */
class CopyAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getData(CommonDataKeys.EDITOR)
        val virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE)
        val project = event.getData(CommonDataKeys.PROJECT)

        if (editor != null && project != null) {
            // selecting in an editor
            val document = editor.document
            val file = PsiDocumentManager.getInstance(project).getCachedPsiFile(document)
            if (file != null) {
                CopyPasteManager
                    .getInstance()
                    .setContents(
                        StringSelection(
                            "http://localhost:63342/api/file/${getFileFqn(file)}:${editor.caretModel.logicalPosition.line + 1}"
                        )
                    )
            }
        } else if(virtualFile != null && project != null) {
            CopyPasteManager
                .getInstance()
                .setContents(
                    StringSelection(
                        "http://localhost:63342/api/file/${getVirtualFileFqn(virtualFile, project)}"
                    )
                )
        }
    }

    private fun getFileFqn(file: PsiFile): String {
        val virtualFile = file.virtualFile
        return if (virtualFile == null) file.name else getVirtualFileFqn(virtualFile, file.project)
    }

    private fun getVirtualFileFqn(virtualFile: VirtualFile, project: Project): String {
        for (provider in VirtualFileQualifiedNameProvider.EP_NAME.extensionList) {
            val qualifiedName = provider.getQualifiedName(project, virtualFile)
            if (qualifiedName != null) {
                return qualifiedName
            }
        }

        val module = ProjectFileIndex.getInstance(project).getModuleForFile(virtualFile, false)
        if (module != null) {
            for (root in ModuleRootManager.getInstance(module).contentRoots) {
                val relativePath = VfsUtilCore.getRelativePath(virtualFile, root)
                if (relativePath != null) {
                    return relativePath
                }
            }
        }

        val relativePath = VfsUtilCore.getRelativePath(virtualFile, project.getBaseDir())
        if (relativePath != null) {
            return relativePath
        }

        val rootType = RootType.forFile(virtualFile)
        if (rootType != null) {
            val scratchRootVirtualFile =
                VfsUtil.findFileByIoFile(File(ScratchFileService.getInstance().getRootPath(rootType)), false)
            if (scratchRootVirtualFile != null) {
                val scratchRelativePath = VfsUtilCore.getRelativePath(virtualFile, scratchRootVirtualFile)
                if (scratchRelativePath != null) {
                    return scratchRelativePath
                }
            }
        }

        return virtualFile.path
    }


    override fun update(event: AnActionEvent) {
    }

}