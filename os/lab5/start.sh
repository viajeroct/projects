#!/bin/bash

file=info
: > $file
watch -n 1 ./monitoring.sh
