myJar=${PWD}/myAnt.jar
proc=`ps -ef | grep -v grep | grep -v "$0"| grep "$myJar" | awk '{print $2}'`
if [ -n "$proc" ]
then
kill -9 $proc
else
echo "no process "$myJar
fi
