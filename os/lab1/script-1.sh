#!/bin/bash

if [[ $(echo "$1>=$2 && $1>=$3" | bc -l) -eq 1 ]]
then echo $1
elif [[ $(echo "$2>=$1 && $2>=$3" | bc -l) -eq 1 ]]
then echo $2
else echo $3
fi
