package de.fholzstein.mb.linux.mod

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

val configurationService = ConfigurationService(File("${System.getProperty("user.home")}/.config/mb_linux_mod/"))

class Tool : CliktCommand(invokeWithoutSubcommand = true) {
    override fun run() {
        if (context.invokedSubcommand == null) {
            val loadConfiguration = configurationService.loadConfiguration("default")
            if (loadConfiguration == null) {
                echo("There are no mods configured.\n" +
                        "If you want to configure the tool run the tool with --help")
            } else {
                echo("Starting copy process of configured mods for library ${loadConfiguration.steamHome}")
                val modDir = File("${loadConfiguration.steamHome}/steamapps/workshop/content/48700")
                val mountAndBladeDir = File("${loadConfiguration.steamHome}/steamapps/common/MountBlade Warband")
                if (!mountAndBladeDir.exists()) {
                    echo("Mount and Blade Warband is not installed in this SteamLibrary")
                } else if (!modDir.exists() || modDir.listFiles().isEmpty()) {
                    echo("There are no M&B Warband mods downloaded in this SteamLibrary", err = true)
                } else {
                    val mountAndBladeModulesDir = File(mountAndBladeDir, "Modules")
                    if (!mountAndBladeModulesDir.exists()) {
                        mountAndBladeModulesDir.mkdirs()
                    }
                    val copyRequests = loadConfiguration.modConfigs.map { it -> CopyDirectoryRequest(File(modDir, it.srcName), File(mountAndBladeModulesDir, it.targetName)) }
                    val copyResults = FileService().copyDirectories(copyRequests)
                    val failedCopies = copyResults.filter { !it.success }
                    for (failedCopy in failedCopies) {
                        echo("Copying Mod with id ${failedCopy.request.srcDir.name} and name ${failedCopy.request.targetDir.name} failed with error:\n" +
                                "${failedCopy.errorMessage}")
                    }
                }
            }
        }
    }

}

class LibraryConfigCommand : CliktCommand("configure steam library", name = "configureLib") {
    private val path by option("-p", "--path", help = "path to the steam library as seen in steam").file().required()

    override fun run() {
        val libraryPath = path


        val loadConfiguration = configurationService.loadConfiguration("default")
        if (loadConfiguration == null) {
            val configuration = Configuration(libraryPath.path)
            configurationService.writeConfiguration(configuration, "default")
        } else {
            val configuration = loadConfiguration.copy(steamHome = libraryPath.path)
            echo("No Configuration found...adding new Configuration")
            configurationService.writeConfiguration(configuration, "default")
        }
        echo("SteamLibrary successfully set to '$path'")
    }
}

class ModConfigCommand : CliktCommand("add or a change a mod", name = "mod") {
    val id by option("-id", help = "ID of the mod in the workshop").required()
    val name by option("-n", help = "Name of the mod as to be displayed in mod selection").required()

    override fun run() {
        val modConfiguration = ModConfiguration(id, name)
        val loadConfiguration = configurationService.loadConfiguration("default")
        if (loadConfiguration == null) {
            val configuration = Configuration(modConfigs = listOf(modConfiguration))
            echo("No previous configuration found, creating new one for SteamLibrary '${configuration.steamHome}'")
            configurationService.writeConfiguration(configuration, "default")
        } else {
            val modsToWrite = loadConfiguration.modConfigs.toMutableList().filter { it -> it.srcName != id }.toMutableList()
            modsToWrite.add(modConfiguration)
            val configuration = loadConfiguration.copy(modConfigs = modsToWrite)
            configurationService.writeConfiguration(configuration, "default")
        }
        echo("Mod with id '$id' and name '$name' successfully added to configuration")
    }

}

class ModSelectionCommand : CliktCommand("Select the mod to start M&B Warband with", name = "selectMod") {
    val name by option("-n", help = "Name of the mod (Directory name inside the Modules folder in M&B directory)").required()
    override fun run() {
        val configuration = configurationService.loadConfiguration("default")
        if (configuration == null || configuration.modConfigs.none { it.targetName == name }) {
            TermUi.confirm("The chosen mod is not configured in this tool do you want to continue?", abort = true)
        }
        val lastModFile = File("${System.getProperty("user.home")}/.mbwarband/last_module_warband")
        if (!lastModFile.exists()) {
            echo("File ${lastModFile.absolutePath} does not exist...creating it")
            lastModFile.mkdirs()
            lastModFile.createNewFile()
        }
        lastModFile.writeText(name)
        echo("Successfully selected $name as the default mod")
    }
}

class ShowConfigCommand : CliktCommand("Show the current configuration", name = "showConfig") {
    override fun run() {
        val configuration = configurationService.loadConfiguration("default")
        if (configuration != null) {
            echo(
                    "SteamLibrary: ${configuration.steamHome}\n" +
                            "Mods: \n" +
                            configuration.modConfigs.map { "\t${it.srcName} : ${it.targetName}" }.reduce { string, it -> string + "\n" + it }
            )
        } else {
            echo("No existing configuration")
        }
    }
}

class RemoveModCommand : CliktCommand("Removes one mod identified by its workshop id", name = "removeMod"){
    val id by option("-id", help = "ID of the mod as seen in the last part of the URL in the Steam Workshop").required()

    override fun run() {
        val configuration = configurationService.loadConfiguration("default")
        if(configuration != null){
            val modsToWrite = configuration.modConfigs.toMutableList().filter { it -> it.srcName != id }
            configurationService.writeConfiguration(configuration.copy(modConfigs = modsToWrite), "default")
            echo("Mod with id $id successfully removed")
        } else {
            echo("No Configuration found to remove the mod from")
        }
    }

}

fun main(args: Array<String>) {
    Tool().subcommands(ModSelectionCommand(), LibraryConfigCommand(), ModConfigCommand(), ShowConfigCommand(), RemoveModCommand()).main(args)
}