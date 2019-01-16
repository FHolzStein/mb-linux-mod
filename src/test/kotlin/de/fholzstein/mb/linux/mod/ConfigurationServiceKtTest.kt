package de.fholzstein.mb.linux.mod

import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.WordSpec
import kotlinx.serialization.json.JSON
import java.io.File

class ConfigurationServiceKtTest  : WordSpec() {

    override fun afterTest(description: Description, result: TestResult) {
        super.afterTest(description, result)
        cleanUpTestFiles()
    }

    init {
        "loadConfiguration" should {
            "successfully load from json file" {
                // given
                val configDir = File(ConfigurationServiceKtTest::class.java.classLoader.getResource("ConfigurationServiceTest").file)
                val testee = ConfigurationService(configDir)

                // when
                val configuration = testee.loadConfiguration("correctConfig")

                // then

                configuration shouldNotBe null
                configuration!!.steamHome shouldBe "someHome"
                configuration.modConfigs.size shouldBe 1

                val modConfiguration = configuration.modConfigs[0]
                modConfiguration.srcName shouldBe "someSrcName"
                modConfiguration.targetName shouldBe  "someTargetName"
            }

            "fail on missing configFile" {
                // given
                val configDir = File(ConfigurationServiceKtTest::class.java.classLoader.getResource("ConfigurationServiceTest").file)
                val testee = ConfigurationService(configDir)
                val configName = "nonexsistingFile"

                // when
                val configuration = testee.loadConfiguration(configName)

                // then
                configuration shouldBe null
            }
        }
        "writeConfiguration" should {
            "write a configuration to a JSON file" {
                // given
                val configDir = createTestDir()
                val testee = ConfigurationService(configDir)
                val configuration = Configuration("steamHome",
                        listOf(
                                ModConfiguration("srcName", "targetName")
                        ))
                val configName = "someConfigName"
                // when
                val result = testee.writeConfiguration(configuration, configName)

                // then
                result.success shouldBe true
                result.errorMessage shouldBe null

                val writtenConfiguration =
                        JSON.parse(Configuration.serializer(), File(configDir, "$configName.json").readText())

                writtenConfiguration shouldBe configuration
            }
        }
    }
}