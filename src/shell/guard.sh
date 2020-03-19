#nohup ./guard.sh "java -version" 10 >/dev/null 2>&1 &

function guard()
{
        while true
        do
        proc=`ps -ef | grep -v grep | grep -v "$0" | grep "$1" | awk '{print $2}'`
        if [ -z "$proc" ]
        then
        	`$1`
        fi
        	sleep $2
        done
}

if [ $# > 0 ]
then
        if [ $# > 1]
        then
        	guard "$1" $2
        else
        	guard "$1" 20
        fi
else
        echo nohup ./guard.sh \"java -version\" 10 ">/dev/null 2>&1 &"
fi