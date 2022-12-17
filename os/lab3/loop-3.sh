#!/bin/bash

a=3451
b=982

echo $$ > pid-loop-3.txt

while true
do
	let x=$a+$b
done
