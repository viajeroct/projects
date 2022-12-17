#!/bin/bash

# -F - устанавливает разделитель, в нашем случае это `:`
# sort:
#   -n - сортировка чисел, а не строк
#   -k - аргумент по которому сортировать (у нас второй)
awk -F : '{print $1 " " $3}' < /etc/passwd | sort -k 2 -n
