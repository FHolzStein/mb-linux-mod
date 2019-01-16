package de.fholzstein.mb.linux.mod

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import java.io.File

@Serializable
data class ModConfiguration(val srcName: String,
                         val targetName: String)

@Serializable
data class Configuration(val steamHome: String = ".steam/steam",
                         val modConfigs: List<ModConfiguration> = listOf())

data class ConfigurationWriteResult(val success: Boolean,
                                    val errorMessage: String? = null)

class ConfigurationService(val configDir: File){

    fun loadConfiguration(configName: String) : Configuration?{
        val configFile = File(configDir, "$configName.json")
        if(!configFile.exists()){
            return null
        }
        return JSON.parse(Configuration.serializer(), configFile.readText())
    }

    fun writeConfiguration(configuration: Configuration, configName: String) : ConfigurationWriteResult {
        val configFile = File(configDir, "$configName.json")
        if(!configFile.parentFile.exists()) {
            configFile.parentFile.mkdirs()
        }
        configFile.writeText(JSON.indented.stringify(Configuration.serializer(),configuration))
        return ConfigurationWriteResult(true)
    }
}