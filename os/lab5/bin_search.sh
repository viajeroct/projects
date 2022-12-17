#!/bin/bash

l=1700000
r=1850000

while [[ $(($r - $l)) -gt 1 ]]
do
	: > is_finished
	mid=$((($r + $l) / 2))
	./launch_ex_2.sh $mid
	while true
	do
		sleep 10
		cur=$(top -n 1 -b | grep "newmem.bash")
		if [[ -z "$cur" ]]
		then
			echo "Calculated $mid!"
			break
		fi
		echo "Waiting for $mid..."
	done
	ok=$(wc -l < is_finished)
	echo "Was finished $ok tasks!"
	if [[ $ok -eq 30 ]]
	then
		l=$mid
		echo "Shift l to $mid."
	else
		r=$mid
		echo "Shift r to $mid."
	fi
	echo "Current l is $l."
done

echo "Answer is $l!"
