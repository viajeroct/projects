#!/bin/bash

msg="catalog test was created successfully"
dir_name="test"
host_name="www.net_nikogo.ru"

# rm -r ./test
# rm ./report

# 2> - switch stderr to file /dev/null
mkdir $dir_name 2> /dev/null && {
	echo $msg > report
	touch test/$(date +'%d.%m.%y-%T')
}

ping $host_name 2> /dev/null || {
	echo "$(date +'%d.%m.%y %T') error connecting to host" >> report
}
