#!/bin/bash

x=5
y=-9
z=10

: > ./ready.txt
for ((i=1; i <= $1; i++))
do
	echo "$x $y $z" | ./task_1 >> ./ready.txt &
	x=$((x+10))
	y=$((y-6))
	z=$((z+12))
done

wait
