#!/bin/bash

if [[ $# -ne 1 ]]
then
	echo "Expected 1 argument, but got $#."
	exit 1
fi

name_of_file=$1
name_of_hidden_dir=/home/.trash

if [ ! -f $name_of_file ]
then
	echo "File doesn't exist."
	exit 1
fi

if [ ! -d $name_of_hidden_dir ]
then
	mkdir $name_of_hidden_dir
fi

link_name=$(date +'%d.%m.%y-%T')
ln $name_of_file "$name_of_hidden_dir/$link_name"
rm $name_of_file

cur_dir=$PWD
echo "$cur_dir/$name_of_file;$link_name" >> /home/.trash.log
