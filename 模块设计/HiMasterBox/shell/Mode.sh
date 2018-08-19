#$/*************************************************************************
#    > File Name: Mode.sh
#    > Author: HHH
#    > Mail: 305837300@qq.com 
#    > Created Time: 2018年08月04日 星期六 15时13分11秒
# ************************************************************************/
#!/bin/bash
case $1 in 
    1)
    echo "starting HOME mode"
    echo "CL+M1+LED+ON" > /home/pi/master/driverCL/CL.txt
    sleep 0.2
    echo "CL+M3+SWH+ON" > /home/pi/master/driverCL/CL.txt
    sleep 0.2
    ;;
    2)
    echo "starting SAFE mode"
    echo "CL+M3+SWH+OFF" > /home/pi/master/driverCL/CL.txt
    sleep 0.2
    echo "CL+M2+STP+OFF" > /home/pi/master/driverCL/CL.txt
    sleep 0.2
    ;;
    3)
    echo "starting SLEEP mode"
    #you can set to yourself like close the light and anything
    ;;
esac    
