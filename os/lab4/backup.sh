#!/bin/bash

cd /home/user/

cur_date=$(date +'%Y-%m-%d')
dir_name="Backup-$cur_date"

last_backup=$(ls |
              grep -E "^Backup-[0-9]{4}-[0-9]{2}-[0-9]{2}" |
              cut -d '-' -f 2,3,4 |
              sort -r -t '-' -nk 1,1 -nk 2,2 -nk 3,3 |
	      head -n 1)

dist=7
if [[ "$last_backup" != "" ]]
then
	dist=$(echo "$(date -d $cur_date +%s)" "$(date -d $last_backup +%s)" |
       	       awk '{print ($1 - $2) / 60 / 60 / 24}')
fi

if [[ $dist -ge 7 ]]
then
	mkdir "Backup-$cur_date"
	cd ./source
	data_list=$(ls)
	for obj in $data_list
	do
		cp -r $obj ../"Backup-$cur_date"
	done
	cd ..
	echo "Backup-$cur_date was created" >> backup-report
	echo "List of data:" >> backup-report
	echo "$data_list" >> backup-report
else
	cd ./source
	data_list=$(ls)
	cd ..
	echo "Backup to Backup-$last_backup, date=$cur_date." >> backup-report
	cd "Backup-$last_backup"
	for obj in $data_list
	do
		if [[ -f $obj || -d $obj ]]
		then
			source_size=$(stat --printf="%s" /home/user/source/$obj)
			backup_size=$(stat --printf="%s" $obj)
			if [[ $source_size -ne $backup_size ]]
			then
				mv $obj "$obj.$cur_date"
				cp -r /home/user/source/"$obj" .
				echo "Renaming $obj to $obj.$cur_date." >> ../backup-report
			fi
		else
			cp -r /home/user/source/"$obj" .
			echo "New file $obj." >> ../backup-report
		fi
	done
fi
