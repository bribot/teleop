#!/bin/bash
cd /home/pi/mjpg-streamer/
./mjpg_streamer -i "./input_uvc.so -n -f 15 -r 176x144" -o "./output_http.so -n -w ./www"

