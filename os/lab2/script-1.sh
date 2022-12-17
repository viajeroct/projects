#!/bin/bash

output="results-task-1.txt"

# u - выбрать процессы пользователя
# o - свой формат вывода
# e - выбрать все процессы
ps -eu root --no-headers | wc -l > $output
ps -eu root -o pid,cmd --no-headers | awk '{print $1 ":" $2}' >> $output
