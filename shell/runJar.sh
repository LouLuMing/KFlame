myJar=${PWD}/myAnt.jar
proc=`ps -ef | grep -v grep | grep -v "$0"| grep "$myJar" | awk '{print $2}'`
if [ -n "$proc" ]
then
kill -9 $proc
fi

echo java -jar $myJar
nohup java -jar $myJar > run.log 2>&1 &
more run.log