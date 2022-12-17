#!/bin/bash

./loop-1.sh &
./loop-2.sh &
./loop-3.sh &

# waiting for starting the loops
sleep 3

# -b - background mode
cpulimit -b -p $(cat pid-loop-1.txt) -l 10

kill $(cat pid-loop-3.txt)
