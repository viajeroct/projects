#!/bin/bash

data=()
while true
do
	data+=(1 2 3 4 5 6 7 8 9 10)
	if [[ ${#data[@]} -gt $1 ]]
	then
		echo "finished" >> ./is_finished
		exit 0
	fi
done
