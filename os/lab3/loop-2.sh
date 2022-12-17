#!/bin/bash

a=345
b=9821

echo $$ > pid-loop-2.txt

while true
do
	let x=$a+$b
done
