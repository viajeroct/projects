#!/bin/bash
while read line
do
	wget $line
done < /mnt/c/dc/homeworks/task1/part5/wget-pdfs

find ./ -type f -exec sh -c '
    case $(head -1 $1) in
    	%PDF*) exit 0;;
	esac
	exit 1
' sh {} \; -print > /mnt/c/dc/homeworks/task1/part5/pdfs.txt

while read line
do
	echo $(stat -c%s $line)
done < /mnt/c/dc/homeworks/task1/part5/pdfs.txt | awk '{print $1}' | awk '{SUM += $1} END {print SUM}'
