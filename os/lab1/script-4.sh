#!/bin/bash

if [[ $HOME = $PWD ]]; then
    echo "$HOME"
    exit 0
else
    # $0 - название этого скрипта вместе
    # с относительным путём до него
    echo "Error: you must run $0 from $HOME"
    exit 1
fi
