#!/bin/bash

output="results-task-1.txt"

# u - ������� �������� ������������
# o - ���� ������ ������
# e - ������� ��� ��������
ps -eu root --no-headers | wc -l > $output
ps -eu root -o pid,cmd --no-headers | awk '{print $1 ":" $2}' >> $output
