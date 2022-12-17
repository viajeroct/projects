#!/bin/bash

echo $$ > ./pid-task-6.txt
answer=1
operation="NAN"

usr1()
{
	operation="+"
}

usr2()
{
	operation="*"
}

trap 'usr1' USR1
trap 'usr2' USR2

while true
do
	case "$operation" in
	"+")
		answer=$(echo $answer "2" | awk '{print $1 + $2}')
		echo $answer
		;;

	"*")
		answer=$(echo $answer "2" | awk '{print $1 * $2}')
		echo $answer
		;;

	"NAN")
		echo "Waiting for '+' or for '*'."
		;;
	esac
	sleep 1
done
