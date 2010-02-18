#!/bin/bash
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

#cd pax; java $JAVA_OPTS -cp .:$SCRIPTS:$SCRIPTS/pax-runner.jar org.ops4j.pax.runner.Run "$@"
if [ "$1" ]
	then
	  case $1 in
        tut1)	printf "Starting Tutorial 1\n"
                cd pax; java $JAVA_OPTS -cp .:$SCRIPTS:$SCRIPTS/pax-runner.jar org.ops4j.pax.runner.Run --args="file:tutorial1.args" $2 $3 $4 $5 $6 $7 $8 $9
                ;;
        tut2)	printf "Starting Tutorial 2\n"
                cd pax; java $JAVA_OPTS -cp .:$SCRIPTS:$SCRIPTS/pax-runner.jar org.ops4j.pax.runner.Run --args="file:tutorial2.args" $2 $3 $4 $5 $6 $7 $8 $9
                ;;
        esac
    else
      printf "Starting Inspector\n"
      cd pax; java $JAVA_OPTS -cp .:$SCRIPTS:$SCRIPTS/pax-runner.jar org.ops4j.pax.runner.Run $2 $3 $4 $5 $6 $7 $8 $9
fi
	

