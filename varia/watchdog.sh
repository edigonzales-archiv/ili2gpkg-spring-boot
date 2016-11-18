#!/bin/bash

# Set up script variables
PID_FILE=/var/run/ili2gpkg/ili2gpkg.pid
HTTP_URL=http://85.25.185.233:8886/ili2gpkg
START_SCRIPT=/etc/init.d/ili2gpkg
PID=`cat $PID_FILE`

# Function to kill and restart application server
function ili2gpkg_restart() {
  $START_SCRIPT stop
  sleep 5 
  kill -9 $PID
  $START_SCRIPT start
}

if [ -d /proc/$PID ]
  then
    # App server is running - kill and restart it if there is no response.
    wget $HTTP_URL -T 1 --timeout=20 -O /dev/null &> /dev/null
    if [ $? -ne "0" ]
      then
      echo Restarting ili2gpkg because $HTTP_URL does not respond, pid $PID
      ili2gpkg_restart
      # else
      # echo No Problems!  
    fi
else
  # App server process is not running - restart it
  echo Restarting ili2gpkg because pid $PID is dead.
  ili2gpkg_restart
fi
