#!/bin/bash

output="results-task-2.txt"

ps -eo pid,cmd | grep "/sbin/" | awk '{print $1}' > $output
