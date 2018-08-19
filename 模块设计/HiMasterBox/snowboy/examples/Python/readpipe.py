import sys
import os
import time
MYPIPE = "/tmp/mypipe"

if os.path.exists(MYPIPE):
    os.remove(MYPIPE)
os.mkfifo(MYPIPE) 
while True:
    rfd = os.open(MYPIPE, os.O_NONBLOCK | os.O_RDONLY )
    print "read pipe %s" %os.read(rfd,7)
    time.sleep(2)
os.close(rfd)