#!/bin/bash
a=$(grep -o ^['1'-'9']['0'-'9']* phone-numbers | awk '{print $1}' | awk 'length($1) >= 2 && length($1) <= 15' | wc -l)
b=$(grep -o +['1'-'9']['0'-'9']* phone-numbers | awk '{print $1}' | awk 'length($1) >= 3 && length($1) <= 16' | wc -l)
c=$(grep -o [' ']['1'-'9']['0'-'9']* phone-numbers | awk '{print $1}' | awk 'length($1) >= 2 && length($1) <= 15' | wc -l)
echo $(($a+$b+$c))
