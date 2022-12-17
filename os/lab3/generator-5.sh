#!/bin/bash

mkfifo pipe
regex='^[0-9]+$'
while true
do
	read line
	echo "$line" > pipe
	if [[ "$line" == "QUIT" ]]
	then
		exit
	fi
	if ! [[ "$line" =~ $regex || "$line" == "+" || "$line" == "*" ]]
	then
		rm pipe
		echo "Enter number or '+' or '*', not $line."
		exit 1
	fi
done
