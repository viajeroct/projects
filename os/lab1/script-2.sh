#!/bin/bash

ans=""
while true; do
    read line
    if [ "$line" = "q" ]; then
        echo $ans
        break
    fi
    ans=$ans$line
done
