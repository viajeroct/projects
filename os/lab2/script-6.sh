#!/bin/bash

info=$(ps -eo pid --no-headers)

for pid in $info
do
	status_file_path="/proc/$pid/status"
	if [[ -f $status_file_path ]]
	then
		grep VmSize $status_file_path | awk '{print $2}'
	fi
done | sort -n -r | head -n 1
