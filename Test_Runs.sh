#!/bin/bash

DIR="test_files/case3/";
EXT=".txt";
CLASSPATH=/home/zeeshan/Desktop/hashing_based/consumer_data_privacy_hba/bin/consumer_data_privacy_hba

cd bin/consumer_data_privacy_hba
echo $CLASSPATH
pwd
sleep 10;

COUNTER=0
for (( i = 1; i <= 14; i++ ))      ### Outer for loop ###
do

    for (( j = ($i +1) ; j <= 15; j++ )) ### Inner for loop ###
    do
	FILE1="$DIR$i$EXT"
	FILE2="$DIR$j$EXT"
	COUNTER=`expr $COUNTER + 1`
	echo "Counter : $COUNTER   :  $FILE1 running with $FILE2"

	echo "Executing JAVA Programs in parallel"
	###############Executing JAVA Programs in parallel
	java  HBA_Server_V2 $FILE1 & (sleep 0.02; java  HBA_Client_V2 $FILE2)
	echo "Executed"



    done

###############echo "Sleeping for 5 ms so that all previous executions are completed ports are unbinded"
sleep 5;
  
done
