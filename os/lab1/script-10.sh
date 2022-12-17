#!/bin/bash

# grep -i - игнорировать размер букв
# uniq -c - добавить встречаемость этих уникальных слов
# tail -3 - показать последние 3 штуки
man bash | grep -o -i -E "[[:alpha:]]{4,}" |\
    sort | uniq -c | sort -k 1 -n | tail -3
