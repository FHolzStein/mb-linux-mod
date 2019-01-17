Circle-CI-Build:[![CircleCI](https://circleci.com/gh/FHolzStein/mb-linux-mod.svg?style=svg)](https://circleci.com/gh/FHolzStein/mb-linux-mod)

# M&B Warband Linux Modfix (Steam Workshop installation)
The purpose of this tool is to automate the correct installation of M&B Warband mods that are installed via the Steam Workshop on a Linux system. For me (and it seems like other people on forums too) the Workshop mods are always installed in a directory which is not recognized by M&B Warband itself. Because i got annoyed by always manually copying the respective mods to the correct directory and renaming them (Steam Workshop id to the real mod name) i thought i could automate that and here we are!

## Table of Contents
* [Installation](https://www.github.com/FHolzStein/mb-linux-mod#installation)
* [Usage](https://www.github.com/FHolzStein/mb-linux-mod#usage)
* [Bugs and Feature Requests](https://github.com/FHolzStein/mb-linux-mod#bugs-and-feature-requests)

## Installation
Download the mb-linux-mod-<version>.tgz file from the [Releases page](https://github.com/FHolzStein/mb-linux-mod/releases) and extract it to a directory of choice on your system. In the bin folder you will find the file **mb-linux-mod** which runs the application. This would be sufficient to use the software itself. If you want a proper installation to access it from anywhere you should add this file to your PATH.

## Usage
### Configuration
#### StemLibrary Path
To configure the path to your SteamLibrary just copy the SteamLibrary path from your Steam settings and run the program like this:
`sh mb-linux-mod configureLib -p <path-to-your-SteamLibrary>` 
#### Configure a mod
To configure a mod to be copied by the tool you need to get the ID of the mod in the Steam Workshop (last part of the URL of the Workshoppage). Then run the program like this:
`sh mb-linux-mod mod -id <Steam Workshop ID> -n <Name to be used in M&B>`
#### Run the copy tool
To execute one run of the copy-step (copies all configured mods and only those) execute the tool without any additional parameters like this:
`sh mb-linux-mod`
If no mods are configured the tool will provide this information. If there were problems with copying specific mods the tool will give a fitting message with an errormessage.
#### Select the mod for the next start of M&B Warband
Configuring the next mod will overwrite or create the `~/.mbwarband/last_module_warband` file and set the specific mod. The configuration is based on the configured name of the mod which is also the mod-directory name inside the Modules-folder inside the M&B Warband installation. To execute the configuration run the tool like this:
`sh mb-linux-mod selectMod -n <Name of the mod>`
## Bugs and Feature Requests
TODO
