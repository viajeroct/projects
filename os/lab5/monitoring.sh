#!/bin/bash

file=info

echo "Monitoring step:" >> $file
top -n 1 -b | awk 'NR == 4 {print "mem " $4 " " $6 " " $8 " " $10}' >> $file
top -n 1 -b | awk 'NR == 5 {print "swap " $3 " " $5 " " $7 " " $9}' >> $file
top -n 1 -b | grep -w "mem.bash" >> $file
top -n 1 -b | grep -w "mem2.bash" >> $file
top -n 1 -b | awk 'NR >= 8 && NR <= 12 {print $0}' >> $file
echo "" >> $file
