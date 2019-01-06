package de.fholzstein.mb.linux.mod

import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.matchers.file.shouldExist
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File
import java.util.*


class FileServiceKtTest : StringSpec() {
    val testFiles: MutableList<File> = arrayListOf()

    override fun afterTest(description: Description, result: TestResult) {
        super.afterTest(description, result)
        testFiles.forEach {
            it.deleteRecursively()
        }
    }

    fun createTestDir() : File {
        val result = File(UUID.randomUUID().toString())
        testFiles.add(result)
        return result
    }

    init {
        "method copyDirectory" {


            "should copy directory recursively to target" {
                // given

                val testee = FileService()
                val testSrc = File(FileServiceKtTest::class.java.classLoader.getResource("FileServiceTest/srcDir").file)
                val testTarget = createTestDir()

                // when
                testee.copyDirectory(testSrc, testTarget)

                // then
                val testTargetSubFile = File(testTarget.path, "testFile.txt")

                testTarget.exists() shouldBe true
                testTarget.listFiles().size shouldBe 1
                testTargetSubFile.shouldExist()
                testTargetSubFile.readText() shouldBe "SUCCESS"

            }

            "should not copy single files" {
                // given

                val testee = FileService()
                val testSrc = File(FileServiceKtTest::class.java.classLoader.getResource("FileServiceTest/srcDir/testFile.txt").file)
                val testTarget = File(UUID.randomUUID().toString())

                // when
                testee.copyDirectory(testSrc, testTarget)

                // then
                testTarget.exists() shouldBe false

            }
        }
    }
}