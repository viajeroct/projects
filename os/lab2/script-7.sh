#!/bin/bash

info=$(ps -eo pid --no-headers)

begin=()
pids=()
lines=()

for pid in $info
do
	io_file_path="/proc/$pid/io"
	line_file_path="/proc/$pid/cmdline"
	if [[ -f $io_file_path && -f $line_file_path ]]
	then
		begin[$pid]=$(grep "rchar" $io_file_path | awk '{print $2}')
		pids[$pid]=$pid
		# tr -d '\0' - удалить символы '\0'
		lines[$pid]=$(cat $line_file_path | tr -d '\0')
	fi
done

echo "sleeping for 60 seconds..."
sleep 1
echo "finish!"

for pid in "${pids[@]}"
do
	io_file_path="/proc/$pid/io"
	end=$(grep "rchar" $io_file_path | awk '{print $2}')
	res=$(echo $end ${start[$pid]} | awk '{print $1-$2}')
	echo $pid ${lines[$pid]} $res
done | sort -n -r -k 3 | head -n 3 | awk '{print $1 ":" $2 ":" $3}'
