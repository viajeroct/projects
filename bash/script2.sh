#!/bin/bash
find ./ -type f -exec sh -c '
    case $(head -1 $1) in
    	?ELF*) exit 0;;
    	#!/bin/bash*) exit 0;;
	esac
	exit 1
' sh {} \; -print
