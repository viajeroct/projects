#!/bin/bash

output="full.log"
input="./X.log"

awk '{
    if ($3=="(WW)" || $3=="(II)")
        print $0
}' < $input | sort -k 3 -r > $output

# -i - по умолчанию sed выводит всё на экран,
# а мы хотим сделать всё на месте
sed -i -e 's/(WW)/(Warning:)/' $output
sed -i -e 's/(II)/(Information:)/' $output

cat $output
