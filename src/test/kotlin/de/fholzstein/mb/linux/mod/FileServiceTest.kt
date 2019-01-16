package de.fholzstein.mb.linux.mod

import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.matchers.file.shouldExist
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.specs.WordSpec
import java.io.File
import java.util.*


class FileServiceKtTest : WordSpec() {
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
        "method copyDirectory" should {


            "copy directory recursively to target" {
                // given

                val testee = FileService()
                val testSrc = File(FileServiceKtTest::class.java.classLoader.getResource("FileServiceTest/srcDir").file)
                val testTarget = createTestDir()
                val request = CopyDirectoryRequest(testSrc, testTarget)

                // when
                val result = testee.copyDirectory(request)

                // then
                val testTargetSubFile = File(testTarget.path, "testFile.txt")

                result.request shouldBe  request
                result.success shouldBe true
                testTarget.exists() shouldBe true
                testTarget.listFiles().size shouldBe 1
                testTargetSubFile.shouldExist()
                testTargetSubFile.readText() shouldBe "SUCCESS"
            }

            "not copy single files" {
                // given

                val testee = FileService()
                val testSrc = File(FileServiceKtTest::class.java.classLoader.getResource("FileServiceTest/srcDir/testFile.txt").file)
                val testTarget = File(UUID.randomUUID().toString())

                val request = CopyDirectoryRequest(testSrc, testTarget)

                // when
                val result = testee.copyDirectory(request)

                // then
                result.request shouldBe request
                result.success shouldBe false
                testTarget.exists() shouldBe false

            }
        }
        "method copyDirectories" should {
            "copy a single directory" {
                val testee = FileService()
                val testSrc = File(FileServiceKtTest::class.java.classLoader.getResource("FileServiceTest/srcDir").file)
                val testTarget = createTestDir()

                val copyDirectoryRequest = CopyDirectoryRequest(testSrc, testTarget)

                // when
                val result = testee.copyDirectories(listOf(copyDirectoryRequest))

                // then
                val testTargetSubFile = File(testTarget.path, "testFile.txt")

                result.size shouldBe 1

                val copyDirectoryResult = result[0]

                copyDirectoryResult.success shouldBe true
                copyDirectoryResult.request shouldBe copyDirectoryRequest
                copyDirectoryResult.errorMessage shouldBe null
                testTarget.exists() shouldBe true
                testTarget.listFiles().size shouldBe 1
                testTargetSubFile.shouldExist()
                testTargetSubFile.readText() shouldBe "SUCCESS"
            }
            "copy multiple directories" {
                val testee = FileService()
                val testSrc = File(FileServiceKtTest::class.java.classLoader.getResource("FileServiceTest/srcDir").file)

                val copyDirectoryRequests = listOf(CopyDirectoryRequest(testSrc, createTestDir()),
                        CopyDirectoryRequest(testSrc, createTestDir()),
                        CopyDirectoryRequest(testSrc, createTestDir()),
                        CopyDirectoryRequest(testSrc, createTestDir()))

                // when
                val result = testee.copyDirectories(copyDirectoryRequests)

                // then
                result.size shouldBe 4

                for (copyDirectoryResult in result) {
                    val copyDirectoryRequest = copyDirectoryResult.request
                    val testTarget = copyDirectoryRequest.targetDir
                    val testTargetSubFile = File(testTarget.path, "testFile.txt")

                    copyDirectoryResult.success shouldBe true
                    copyDirectoryRequest shouldBe copyDirectoryRequest
                    copyDirectoryResult.errorMessage shouldBe null
                    testTarget.exists() shouldBe true
                    testTarget.listFiles().size shouldBe 1
                    testTargetSubFile.shouldExist()
                    testTargetSubFile.readText() shouldBe "SUCCESS"
                }
            }
            "not copy single files" {
                // given

                val testee = FileService()
                val testSrc = File(FileServiceKtTest::class.java.classLoader.getResource("FileServiceTest/srcDir/testFile.txt").file)
                val testTarget = File(UUID.randomUUID().toString())

                val request = CopyDirectoryRequest(testSrc, testTarget)

                // when
                val result = testee.copyDirectories(listOf(request))

                // then
                result.size shouldBe 1
                val copyDirectoryResult = result[0]
                copyDirectoryResult.request shouldBe request
                copyDirectoryResult.success shouldBe false
                testTarget.exists() shouldBe false

            }
        }
    }
}