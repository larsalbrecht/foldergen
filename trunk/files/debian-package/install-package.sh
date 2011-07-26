#!/bin/bash

if [ $1 -eq 'gui' ] ; then
    sudo dpkg -i foldergen.deb
else 
    sudo apt-get remove foldergen
fi
