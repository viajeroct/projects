#!/bin/bash

operation="+"
answer=1
regex='^[0-9]+$'

(tail -f pipe) |
while true
do
	read line

	if [[ "$line" == "QUIT" ]]
	then
		rm pipe
		echo "Stop of receiver-5.sh"
		killall tail
		exit
	fi

	if ! [[ "$line" =~ $regex || "$line" == "+" || "$line" == "*" ]]
	then
		echo "Wrong input from generator."
		exit 1
	fi

	if [[ "$line" == "+" || "$line" == "*" ]]
	then
		operation="$line"
	else
		if [[ "$operation" == "+" ]]
		then
			answer=$(echo $answer $line | awk '{print $1 + $2}')
		else
			answer=$(echo $answer $line | awk '{print $1 * $2}')
		fi
		echo $answer
	fi
done
