#!/bin/bash

file=info
parsed=table_info
: > table_info

cat $file | grep "1643 root " | awk '{print $9}' >> $parsed
