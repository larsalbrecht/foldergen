

# Changelog #
<a href='Hidden comment: 
_(work in progress: *100%*)_
'></a>

## Version 1.6.1.0 - coming soon ##
  * Added parameter -pxu | -proxyurl to specify a specific proxy _(work in progress: **50%**)_

## Version 1.6.0.0 ##
> <a href='Hidden comment: * Added parameter -o / -overwrite to select an option what is to do with existing files / folders _(work in progress: *0%*)_
* off - Overwrite nothing (default)
* force - Overwrite all
* ask - Ask what to do for every file'></a>
  * Fixed filemarker "~". Downloaded a file not completely
  * Added chainworing to do more then one job for a file (see [Plugins](Plugins.md))
  * New files that will be copied or downloaded with filemarker "~" and are .zip/.jar-Files and has no title will be deleted automatically after automatic extract
  * Added parameter -cn / -create to create a new foldergenconf with files and folders from the filesystem
  * Fixed help. Now the parameters interpreted languages ​​even if the help-parameter is set
  * changes for Version 1.5.0.1 will be integrated here
  * Added marker ">" to define a zip-file to extract
  * Recode the GUI and add treeview to:
    * view the information
    * change the information
    * create the new files and folders with the changed config
    * export the information to new config
  * Added Translations
    * Italian (Google Translator)
  * Simple change of the plugin framework (version 1.5.1.0)
    * see: [Plugins](Plugins.md)

## Version 1.5.0.1 - discontinued ##
  * Refactored (in parts)
  * Removed basicInfoArr in Plugins. Use `HashMap<Integer, String>` in future

## Version 1.5.0.0 ##
  * Fixed licence in file headers
  * Restructure plugin module
  * Plugins can now modify the config
  * Fixed bug while creating two folders with files on the same direcotry in another directory
  * Restructure source packages
  * Added function to marker "~". Define a Web-URL to retrieve content from the web
  * Added use of proxy (detect automatically)
    * Added parameter -px / -proxy to activate proxy search
  * Add plugin functionality (on/off) to the GUI
  * Add confirmation (on/off) to the GUI
  * Reorganize GUI
  * Reorganize code

## Version 1.4.9.0 ##
  * Added simple [plugin](Plugins.md) - Environment to add an own content marker replacer
  * Added parameter -p to load the plugin module
_note: this is alpha_

## Version 1.4.8.0 ##
  * Added [marker](foldergenconf#Markers_(Version_1.2_and_above).md) ${func.getfilecontent(filepath)} to replace the marker with the content of the given file
  * Added marker "~" to define a copy of a file / folder in the [foldergenconf](foldergenconf.md)
  * Fixed small issues (bugs) in output
  * improved GUI
    * Added "Select root folder" button
    * Added "Start" button

## Version 1.4.7.0 ##
  * Changed [marker](foldergenconf#Markers_(Version_1.2_and_above).md) ${func.counter(X)} to get a startnumber. e.g.:${func.counter(x|10)} to start with 10
  * Changed [marker](foldergenconf#Markers_(Version_1.2_and_above).md) ${func.counter(X)} to get the name of the marker can be a word and not only a character

## Version 1.4.6.1 ##
  * Removed debug message

## Version 1.4.6.0 ##
  * Added parameter -r | -root to add a root path to specify the root of the created folders and files
  * Added parameter -co | -confirmation to add a question before the files and folders will be written to the filesystem
  * Added confirmation dialog for the gui and the CLI
  * Added parameter -l | -locale to set the application language
    * en (English)
    * de (German)
    * fr (French)
    * ru (Russia)

## Version 1.4.5.0 ##
  * Added properties with all output strings
  * Added Translations
    * (English (Native speaker))
    * German (Native speaker)
    * French (Google Translator)
    * Russia (Google Translator)
  * Added use of System Look and Feel in GUI-Mode, if available
  * Prevent some errors to be shown if debug = 0
  * Fixed some BSD-licence header in source files

## Version 1.4.0.1 ##
  * Bugfix: GUI could not started in 1.4.0.0

## Version 1.4.0.0 ##
  * New startup parameter (using [Apache Common CLI](http://commons.apache.org/cli/)) to prevent errors

## Version 1.3.0.0 ##
  * Added simply GUI to choose the file using a "File Choose Dialog"
  * Added some start parameter to choose between GUI and non GUI.

## Version 1.2.0.0 ##
  * Added [markers](foldergenconf#Markers_(Version_1.2_and_above).md) in template content to replace

## Version 1.0.0.0 ##
Generator with following features:
  * generate folders ...
  * generate files ...
  * generate content in files ...
... from config file