#!/bin/bash

sudo cat /var/log/*.log | wc -l

# второй вариант:
# -maxdepth - глубина поиска
# -name - рег. выражение для названия файла
# -exec - то, что выполнить
# wc -l - кол-во строк
# sudo find /var/log -maxdepth 1 -name "*.log" -exec cat {} \; | wc -l
