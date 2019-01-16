package de.fholzstein.mb.linux.mod

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import java.io.File

val configurationService = ConfigurationService(File("${System.getProperty("user.home")}/.config/mb_linux_mod/"))

class Tool : CliktCommand(invokeWithoutSubcommand = true){
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
                } else if(!modDir.exists() || modDir.listFiles().isEmpty()) {
                    echo("There are no M&B Warband mods downloaded in this SteamLibrary", err = true)
                }  else {
                    val mountAndBladeModulesDir = File(mountAndBladeDir, "Modules")
                    if(!mountAndBladeModulesDir.exists()){
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
            configurationService.writeConfiguration(configuration, "default")
        }
    }
}

class ModConfigCommand : CliktCommand("add or a change a mod", name = "mod"){
    val id by option("-id", help = "ID of the mod in the workshop").required()
    val name by option("-n", help = "Name of the mod as to be displayed in mod selection" ).required()

    override fun run() {
        val modConfiguration = ModConfiguration(id, name)
        val loadConfiguration = configurationService.loadConfiguration("default")
        if (loadConfiguration == null) {
            val configuration = Configuration(modConfigs = listOf(modConfiguration))
            configurationService.writeConfiguration(configuration, "default")
        } else {
            val modsToWrite = loadConfiguration.modConfigs.toMutableList().filter { it -> it.srcName != id }.toMutableList()
            modsToWrite.add(modConfiguration)
            val configuration = loadConfiguration.copy(modConfigs = modsToWrite)
            configurationService.writeConfiguration(configuration, "default")
        }
    }

}


fun main(args: Array<String>)  {
    Tool().subcommands(LibraryConfigCommand(), ModConfigCommand()).main(args)
}