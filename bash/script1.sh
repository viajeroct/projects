#!/bin/bash
ls -lt -r | awk '{print $9}' > /mnt/c/dc/homeworks/task1/part3/sorted.txt

while read line
do
	cat /mnt/c/dc/homeworks/task1/part3/answer.txt $line >> /mnt/c/dc/homeworks/task1/part3/answer.txt
done < /mnt/c/dc/homeworks/task1/part3/sorted.txt

sha256sum /mnt/c/dc/homeworks/task1/part3/answer.txt > /mnt/c/dc/homeworks/task1/part3/out.txt
