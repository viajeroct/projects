#!/bin/bash

cd /home/user/

cur_date=$(date +'%Y-%m-%d')
last_backup=$(ls |
              grep -E "^Backup-[0-9]{4}-[0-9]{2}-[0-9]{2}" |
              cut -d '-' -f 2,3,4 |
              sort -r -t '-' -nk 1,1 -nk 2,2 -nk 3,3 |
	      head -n 1)
last_backup="Backup-$last_backup"

if [[ ! -d restore ]]
then
	mkdir restore
fi

cd /home/user/"$last_backup"
for obj in $(ls)
do
	pattern=".[0-9]{4}-[0-9]{2}-[0-9]{2}"
	if ! [[ $obj =~ $pattern ]]
	then
		cp -r $obj /home/user/restore
	fi
done
