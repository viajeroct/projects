#!/bin/bash

n=$1
k=30

for ((i = 0; i < k; i++))
do
	sleep 1
	./newmem.bash $n &
done
