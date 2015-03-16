


# foldergenconf #
The foldergenconf is the config file for FolderGen. You can create your own foldergenconf by using a simple texteditor like (g)vim, notepad, notepad++, scite or something like that. The file can not be UTF8 with byte order mark (BOM). If you create this kind of file, the first layer will not be created. Will be fixed in future. If you need to use UTF8, then use plain UTF8 files without BOM.


## foldergenconf content ##

Example 01:
```
+ My Root Folder
	- a sample file
	- another file
	+ A subfolder in My Root Folder
		+ A subfolder in a subfolder
		- A file in a subfolder
	- another file2
	~ a file copy -> /home/myfile
	~ a folder copy -> /home/myfolder/
		- a file in folder copy
	~ a file from web -> http://www.example.com/examplefile
	> /home/myzipfile.zip
```

Each line has a specified easy syntax. Each whitespace (eg. \t or \n) is used to indent the structure. Be carefully: use only one whitespace to indent the structure.
Then it starts a file (-), a copy<sup>1</sup> (~), a zip<sup>2</sup> (>) or a folder (+). A zip-File must end with .jar or .zip. If you link to a zip file, you can not create new files or folders in new generated folders:
**This is not allowed:**
```
> /home/myzipfile.zip
	- newfile
```

If a file exists, it will be **NOT** overwritten.

You can specify a new (copied) file / folder in a copied folder on the regular way.

If you want so specify template content for a file, you can use "(((" and ")))" to limit this in the foldergenconf-file.

If you specify a file1 in file0, the file1 will **NOT** be written.

```
+ My Root Folder
	+ subfolder 1
		- file1
		(((
		/*************************
		 * This is a template
		 ************************/
		 You can specify this text inside the foldergenconf-file.
		 This content will be pasted in file1 in subfolder1 (subfolder of My Root Folder)
		)))
		- file2
+ Another Root Folder
	+ Another Subfolder
		- file3
		(((
		Another content for another file.
		)))
```

### Markers (Version 1.2 and above) ###
You can use markers inside the template content.
  * ${user.name} - returns the username
  * ${user.homedir} - returns the user home dir
  * ${system.os} - returns the operating system
  * ${file.currentdir} - returns the current dir
  * ${file.name} - returns the current filename
  * ${date.year} - returns the current year as decimal
  * ${date.month} - returns the current month as decimal
  * ${date.day} - returns the current day as decimal
  * ${date.hour} - returns the current hour as decimal
  * ${date.minute} - returns the current minute as decimal
  * ${date.second} - returns the current second as decimal
  * ${date.isleap(YEAR)} - returns true or false
  * ${date.formatcurrent([FORMAT](Format.md))} - returns the current formatted date/time
  * ${func.counter(VAR)} - counter for a VAR
  * ${func.counter(VAR|STARTNUMBER)} - counter for a VAR with startnumber STARTNUMBER
  * ${func.getfilecontent(filepath)} - return the content of the given file
  * ... more comming soon ...

Example 01:
```
+ My Root Folder
	- file-additional
		(((
		Additional Text
		)))
	- file
		(((
		/*************************
		 * This is a template ${file.name}
		 * author: ${user.name} @ 
		 * ${date.day}.${date.month}.${date.year}
		 * ${date.hour}:${date.minute}:${date.second}
		 * leapyear: ${date.isleapyear(${date.year})}
		 * ${date.formatcurrent(d.M.yy)}
		 ************************/
		 You can specify this text inside the foldergenconf-file.
		 This content will be pasted in file1 in subfolder1 (subfolder of My Root Folder)
		 ${func.counter(a)} : Zero
			${func.counter(b)} : Zero Zero
			${func.counter(b)} : Zero First
			${func.counter(b)} : Zero Second
		 ${func.counter(a)} : First
		 ${func.counter(a)} : Second

		 ${func.counter(c|10)} : Ten
			${func.counter(d|1)} : Ten Zero
			${func.counter(d)} : Ten Second
			${func.counter(d)} : Ten Third
		 ${func.counter(c|20)} : Twenty
		 ${func.counter(c)} : Eleven (first counter takes the win)
		 ${func.getfilecontent(C:\My Root Folder\file-additional)}


```


---

<sup>1</sup> : version 1.4.8.0 and above. From version 1.5.0.0 you can specify a URL to get the content in the new file (URL must start with http:// or https://).

<sup>2</sup> : version 1.5.1.0 and above.