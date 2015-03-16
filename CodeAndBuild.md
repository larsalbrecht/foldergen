# Introduction #

I develop FolderGen using Eclipse (Helios), Java 1.6 and Subversion (SVN).

# Eclipse #
At now, I use the following Eclipse-plugins:
+ FatJar (to create the jar with the whole files. Like: FolderGenPluginFramework-jar)
+ Sublicpse (to check out and handle the project)


# SVN #
In SVN my project has following structure:
```
+ SVN
 + trunk
  ...
 + branches
 + tags
  + FolderGenPluginFramework
   ...
  + FolderGenPlugins
   ...
```
In trunk there is the code from FolderGen. In tags the other used "projects".

# build #
If you want to build FolderGen, you need to check out _trunk_ and tags -> _FolderGenPluginFramework_.

In eclipse you can create two projects, one for each named above.

At the build-path, you must add the foldergenpluginframeworkjar-current-full.jar to resolve the classes.

Now, if all classes are found and no erros are there, you can run FolderGen. To build the jar-file, you can use the **.jardesc files.**But be carefully: Eclipse do not integrate the foldergenpluginframeworkjar-current-full.jar to the created jar.

Now you have two choices: Copy the foldergenpluginframework-files to your FolderGen-src directory, or you use the Eclipse-Plugin "FatJar".

FatJar adds a new option to your context menu: Build Fat Jar
Click it and give the jar a name. Then you can create a new manifest for all next builds. Then you must choose the Main-Class. Select "One-JAR" and click "Next >".
Now you can check the added "FolderGenPluginFramework"-Jar to Fat Jar. Press Finish and test ;)