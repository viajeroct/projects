#!/bin/bash

for ((n=1; n <= 20; n++))
do
	sum=0
	for ((i=1; i <= 10; i++))
	do
		res=`{ \time -f "%e" ./launch_2.sh $n ; } 2>&1 1>/dev/null`
		sum=$(echo $sum $res | awk '{print $1 + $2}')
	done
	sum=$(echo $sum 10 | awk '{print $1 / $2}')
	echo "$sum"
done
