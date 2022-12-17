#!/bin/bash

for ((i=1; i <= $1; i++))
do
	echo "file_$i" | ./task_2 > /dev/null
done
