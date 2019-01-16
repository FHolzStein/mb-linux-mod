package de.fholzstein.mb.linux.mod

import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.WordSpec
import java.io.File

class ConfigurationServiceKtTest  : WordSpec() {

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
                configuration.steamHome shouldBe "someHome"
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
                val exception = shouldThrow<IllegalArgumentException> {
                    testee.loadConfiguration(configName)
                }

                // then
                exception.message shouldBe "No configuration found for $configName"
            }
        }
    }
}