# java-bootstrapper
Bootstrapping library for java to allow dependency updates and isolated loading of dependencies after initialisation.

## Usage
First you need to customise the configuration file in `src/main/resources/config.properties` (all configuration files are simple key=value property files.
```
baseDir = {directory to install into}
versionUrl = {URL to check for version updates}
```
The bootstrapper can be built with `mvn clean package` and the executable jar used to run your application.

Before running, the entry in **versionUrl** should hold a configuration file of the following format.
```
latest.version = {version to install - simple integer, starting at 1}
latest.downloadFile = {zip file containing all the dependencies required to run your application - no nested directories}
latest.mainClass = {class used to start your application}
```

## How It Works
When the bootstrapper starts, the following actions are taken;
* **baseDir** is checked for a `version.properties` file, if not found, current version is assumed to be 0
* **versionUrl** is checked and if the **latest.version** > **local.version** an upgrade starts
  * **latest.downloadFile** is downloaded and unpacked into **baseDir/lib/{VERSION}**
  * `version.properties` file is updated to include **local.version** and **local.mainClass** (taken from **latest.version** and **latest.mainClass** in **versionUrl** property file)
* Any jar files within **baseDir/lib/{VERSION}** are loaded and the main method from **local.mainClass** is executed with an empty String[] passed in

Application updates can be performed by updating the property file at **versionUrl** (remembering to increment the version) and creating a new zip file of dependencies - located at **latest.downloadFile**

## Future Updates
* Tool to help build the property file located at **versionUrl**
* Signing for boostrapper and update files
