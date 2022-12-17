#!/bin/bash

input="results-task-4.txt"
output="results-task-5.txt"
: > $output

last_par=$(head -n 1 $input | awk -F '=' '{print $3}' | awk -F ':' '{print $1}')
sum_art=0
cnt=0

while read line
do
	cur_par=$(echo $line | awk -F '=' '{print $3}' | awk -F ':' '{print $1}')
	cur_art=$(echo $line | awk -F '=' '{print $4}')
	if [[ $last_par -eq $cur_par ]]
	then
		cnt=$(echo $cnt 1 | awk '{print $1+$2}')
		sum_art=$(echo $sum_art $cur_art | awk '{print $1+$2}')
	else
		res=$(echo $sum_art $cnt | awk '{print $1/$2}')
		echo "Average_Running_Children_of_ParentID=$last_par is $res" >> $output
		last_par=$cur_par
		sum_art=$cur_art
		cnt=1
	fi
	echo $line >> $output
done < $input

res=$(echo $sum_art $cnt | awk '{print $1/$2}')
echo "Average_Running_Children_of_ParentID=$last_par is $res" >> $output
