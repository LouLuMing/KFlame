while [ 1 ]
do
if [ $# = 1 ]
then
proc=`ps -ef | grep -v grep | grep -v "$0"| grep "$1" | awk '{print $2}'`
if [ -n "$proc" ]
then
kill -9 $proc
fi
else
echo usage: loopKillProcess.sh name
fi
done

