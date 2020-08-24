cd `dirname $0`

myJar=${PWD}/myAnt.jar
mainClass="com.china.fortune.myant.MyAnt"
javaParam="-Xms512m -Xmx3g -Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider"
javaLib="-Djava.ext.dirs=./libs"
#javaLib=`$mainProc tools.ShowLibs`

mainProc="java -cp "$myJar
guardShell=${PWD}/guard.sh
errMsg="./service.sh start | stop"
missMsg="miss "$myJar

function start()
{
    ${PWD}/killprocess.sh "$guardShell"
    ${PWD}/killprocess.sh "$mainProc"
    echo "$guardShell" "$mainProc $javaLib $javaParam $mainClass"
    nohup "$guardShell" "$mainProc $javaLib $javaParam $mainClass" > /dev/null 2>&1 &
}

function stop()
{
    ${PWD}/killprocess.sh "$guardShell"
    ${PWD}/killprocess.sh "$mainProc"
}

if [ -f $myJar ]
then
#    $mainProc com.china.fortune.restfulHttpServer.maintain.SaveStatistics
    if [ $# = 1 ]
    then
        if [ "$1" = "start" ]
        then
            start
        else
            if [ "$1" = "stop" ]
            then
                stop
            else
                echo "$errMsg"
            fi
        fi
    else
        echo "$errMsg"
    fi
else
	echo "$missMsg"
fi

