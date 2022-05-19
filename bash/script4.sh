#!/bin/bash
find ./ -name "*UdiVC*" -exec sh -c '
	wc -w $1
' sh {} \; | awk '{print $1}' | awk '{SUM += $1} END {print SUM}'
