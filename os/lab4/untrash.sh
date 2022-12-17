#!/bin/bash

if [[ $# -ne 1 ]]
then
	echo "Expected 1 argument, but got $#."
	exit 1
fi

name_of_file=$1
trash_info=$(cat /home/.trash.log)

: > /home/.trash.log

for line in $trash_info
do
	tmp="$(echo "$line" | cut -d ';' -f 1)"
	trash_name="$(echo "$line" | cut -d ';' -f 2)"
	file_name="$(echo $tmp | awk -F / '{print $NF}')"
	file_name_len=${#file_name}
	file_dir=${tmp::-$file_name_len}
	put_back="yes"
	if [[ $file_name == $name_of_file ]]
	then
		read -r -p "Do you want to restore $tmp? (y/n) " answer
		if [[ $answer == "y" ]]
		then
			cd /home/.trash
			if [ ! -d $file_dir ]
			then
				echo "$file_dir doesn't exist, restoring in /home/"
				file_dir=/home/
			fi
			if [ ! -f $file_dir$file_name ]
			then
				ln "$trash_name" "$file_dir$file_name"
				rm "$trash_name"
				put_back="no"
			else
				read -r -p "File with such name already exists. Do you want to rename it? (y/n) " answer
				if [[ $answer == "y" ]]
				then
					read new_name
					ln "$trash_name" "$file_dir$new_name"
					rm "$trash_name"
					put_back="no"
				fi
			fi
		fi
	fi
	if [[ $put_back == "yes" ]]
	then
		echo $line >> /home/.trash.log
	fi
done
