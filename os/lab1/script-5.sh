#!/bin/bash

# файл скопировал с Cent OS из виртуалки
input="./syslog"
output="info.log"
awk '{
    if ($2=="INFO")
        print $0
}' < $input > $output
