package de.fholzstein.mb.linux.mod

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.IOException

data class CopyDirectoryRequest(val srcDir: File,
                                val targetDir: File)

data class CopyDirectoryResult(val request: CopyDirectoryRequest,
                               val success: Boolean,
                               val errorMessage: String? = null)

class FileService {
    fun copyDirectories(requests: List<CopyDirectoryRequest>): List<CopyDirectoryResult> = runBlocking {
        requests.map {
            async {
                copyDirectory(it)
            }
        }.awaitAll()
    }

    fun copyDirectory(request: CopyDirectoryRequest) : CopyDirectoryResult {
        val (srcDir, targetDir) = request;
        if (!srcDir.isDirectory) {
            return CopyDirectoryResult(request, false, "The source dir ${srcDir.absolutePath} is not a directory")
        }
        srcDir.copyRecursively(targetDir, true)
        return CopyDirectoryResult(request, true)
    }
}