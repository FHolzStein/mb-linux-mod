package de.fholzstein.mb.linux.mod

import java.io.File
import java.util.*


val testFiles: MutableList<File> = arrayListOf()
fun createTestDir() : File {
    val result = File(UUID.randomUUID().toString())
    testFiles.add(result)
    return result
}

fun cleanUpTestFiles() = testFiles.forEach { it.deleteRecursively() }