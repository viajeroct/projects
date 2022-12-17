#!/bin/bash

regex="^[0-9]+$"
while true
do
	read line
	case $line in
		TERM)
		kill -SIGTERM $(cat ./pid-task-6.txt)
		exit 0
		;;
	"+")
		kill -USR1 $(cat ./pid-task-6.txt)
		;;
	"*")
		kill -USR2 $(cat ./pid-task-6.txt)
		;;
	esac
done
