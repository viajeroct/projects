#!/bin/bash

output="results-task-4.txt"

info=$(ps -eo pid --no-headers)

for pid in $info
do
	ppid_path="/proc/$pid/status"
	time_info_path="/proc/$pid/sched"
	if [[ -f $ppid_path && -f $time_info_path ]]
	then
		ppid=$(grep PPid $ppid_path | awk '{print $2}')
		sum=$(grep se.sum_exec_runtime $time_info_path | awk '{print $3}')
		switches=$(grep nr_switches $time_info_path | awk '{print $3}')
		art=$(echo $sum $switches | awk '{print $1/$2}')
		echo "ProcessID=$pid : Parent_ProcessID=$ppid : Average_Running_Time=$art"
	fi
done | sort -t '=' -n -k3 > $output
# -t '=' - разделитель =
