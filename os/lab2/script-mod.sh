#!/bin/bash

info=$(ps -eo pid --no-headers)

for pid in $info
do
	time_info_path="/proc/$pid/sched"
	if [[ -f $time_info_path ]]
	then
		echo "$pid $(grep se.sum_exec_runtime $time_info_path | awk '{print $3}')"
	fi
done | sort -n -r -k 2 | head -n 2 | awk '{print $1}'
