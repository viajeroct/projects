#!/bin/bash

while  true
do
	read index
	if [[ $index -eq 1 ]]
	then nano
	elif [[ $index -eq 2 ]]
	then vi
	elif [[ $index -eq 3 ]]
	then links
	else break
	fi
done
