#!/bin/bash

a=345
b=982

echo $$ > pid-loop-1.txt

while true
do
	let x=$a+$b
done
