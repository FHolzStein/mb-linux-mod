package de.fholzstein.mb.linux.mod

import java.io.File

class FileService {
    fun copyDirectory(srcDir: File, targetDir: File) {
        if (!srcDir.isDirectory || targetDir.isDirectory){
            return
        }
        srcDir.copyRecursively(targetDir, true)
    }
}