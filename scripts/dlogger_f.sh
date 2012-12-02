#!/bin/sh

DIR=`dirname $0`
. ${DIR}/common.sh

DIRECT=true
NAME="dgflogger_f+d"
if [ -n "$3" ]; then
	DIRECT="$3"
	NAME="dgflogger_f-d"
fi

#run $NAME dgflogger_f "$1" "$2" org.gflogger.perftest.DFormattedLoggerExample "-Dgflogger.direct=$DIRECT -Dgflogger.filename=./logs/dgflogger_F.log -Dgflogger.service.count=1024 -Dgflogger.append=false -ea -Dgflogger.internalDebugEnabled=true -Dgflogger.bufferedIOThreshold=100000"
run $NAME dgflogger_f "$1" "$2" org.gflogger.perftest.DFormattedLoggerExample "-Dgflogger.direct=$DIRECT -Dgflogger.filename=./logs/dgflogger_f.log -Dgflogger.service.count=1024 -Dgflogger.append=false -Dgflogger.internalDebugEnabled=false"
