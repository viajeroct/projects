#!/bin/bash

: > report2.log

step=0
data=()
while true
do
	data+=(1 2 3 4 5 6 7 8 9 10)
	step=$(($step + 1))
	if [[ $step -eq 100000 ]]
	then
		echo ${#data[@]} >> report2.log
		step=0
	fi
done
