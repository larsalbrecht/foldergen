


# folderGen #
FolderGen is to generate automatically folders and files using a config file (called: foldergenconf). You can specify content for files to fill them automatically.

## What is folderGen? ##
FolderGen is a Java Application (Command Line and GUI) that can generate folders and files (with content) automatically.

## Versioning ##
`1.2.3.4`
  * 1
    * Increments if code is refactored, bigger features added or I _feel_ if this must grow up.
  * 2
    * Increments if a new feature (small to medium) is added or a feature is revised.
  * 3
    * Increments if a small (but bigger) bug is fixed.
  * 4
    * Increments if a small bug is fixed, tiny feature or change or the code is refactored (in parts).

## General information to start ##
If you have the .jar file on you filesystem, you need a .[foldergenconf](foldergenconf.md)-File. This file can be written in a default text editor like vim, notepad or something like that. For more about the foldergenconf, look at the wiki-site.

### Installation ###

  1. Only download the jar from "Downloads"
  1. On Debian / Ubuntu (etc.) you can download the .deb package and install the new version _(... coming soon ...)_

### Start folderGen (Version 1.2.0.0 and lower) ###
```
java -jar foldergen.jar C:\<filename>.foldergenconf
```
or
```
java -jar foldergen.jar /home/testuser/<filename>.foldergenconf
```

### Start folderGen (Version 1.2.0.0 to Version 1.3.0.0) ###
```
java -jar foldergen.jar C:\<filename>.foldergenconf
```
or
```
java -jar foldergen.jar /home/testuser/<filename>.foldergenconf
```


#### More Examples ####
Start with gui and file (without debug):
```
java -jar foldergen.jar C:\<filename>.foldergenconf true
```

Start with gui and file (with debug):
```
java -jar foldergen.jar C:\<filename>.foldergenconf true true
```

Start without gui and with file (without debug):
```
java -jar foldergen.jar C:\<filename>.foldergenconf
```

Start without gui and with file (with debug):
```
java -jar foldergen.jar C:\<filename>.foldergenconf false true
```

Start with gui and without file (without debug):
```
java -jar foldergen.jar true false
```

Start with gui and without file (with debug):
```
java -jar foldergen.jar true true
```

Start FolderGen without file and without gui is not allowed.


### Start folderGen (Version 1.4.0.0 and above) ###
From version 1.4.0.0 parameters are read differently than before. As of now there are the following parameters, which values can be given:

  * -c,-config `<arg>`  	Config file (**.foldergenconf)
  * -d,-debug         		Show debug prints
  * -g,-gui           		use GUI
  * -h,-help          		Show this help entry
  * -co,-confirmation 		Shows a confirmation dialog before writing (added in 1.4.6.0)
  * -l,-locale `<arg>` 	Set the locale to change the application language (added in 1.4.6.0)
  * -r,-root `<arg>` 	Set the root folder to start writing in here (added in 1.4.6.0)
  * -p,-plugins       		Set if you want to load additional plugins (added in 1.4.9.0)
  * -px,-proxy        		Set if you want to let FolderGen search for a default proxy (added in 1.5.0.0)**<a href='Hidden comment:  * -o,--overwrite <arg>  	Set if you want to overwrite existing files (added in 1.6.0.0)'></a>


```
java -jar foldergen.jar -c C:\<filename>.foldergenconf
```
or
```
java -jar foldergen.jar -c /home/testuser/<filename>.foldergenconf
```

#### More Examples ####
Start with gui and file (without debug):
```
java -jar foldergen.jar -c C:\<filename>.foldergenconf -g
```

Start with gui and file (with debug):
```
java -jar foldergen.jar -c C:\<filename>.foldergenconf -g -d
```

Start without gui and with file (without debug):
```
java -jar foldergen.jar -config C:\<filename>.foldergenconf
```

Start without gui and with file (with debug):
```
java -jar foldergen.jar -c C:\<filename>.foldergenconf -debug
```

Start with gui and without file (without debug):
```
java -jar foldergen.jar -gui
```

Start with gui and without file (with debug):
```
java -jar foldergen.jar -g -debug
```

Start with plugins (available in version 1.4.9.0 and above) and with gui (with debug):
```
java -jar foldergen.jar -g -p
```

Start FolderGen without file and without gui is not allowed.

### In Eclipse as "external tools" ###
In Eclipse you can specify external tools. It is easy to configure.

  1. Open the "External Tools Configurations" menu
  1. Location: ${env\_var:JAVA\_HOME}/bin/java.exe
  1. Working Directory: `<any>`
  1. Arguments: -jar <path/to/>foldergenjar.jar

You can specify any additional foldergen parameter behind "`-jar <path/to/>foldergenjar.jar`".