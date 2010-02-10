#!/bin/sh
#
# Script to run Pax Runner, which starts OSGi frameworks with applications.
#
#

SCRIPTS=`readlink $0`
if [ "${SCRIPTS}" != "" ]
then
  SCRIPTS=`dirname $SCRIPTS`
else
  SCRIPTS=`dirname $0`
fi

cd pax; java $JAVA_OPTS -cp .:$SCRIPTS:$SCRIPTS/pax-runner.jar org.ops4j.pax.runner.Run "$@"