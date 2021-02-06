#!/bin/bash
export DISPLAY=:0 
PATH=$PATH:/home/leonardo/Downloads/chromedriver_linux64/chromedriver; 
java -jar /home/leonardo/palmeiro/kirolak/kirolak.jar  /home/leonardo/palmeiro/kirolak/kirolak.log 2> /home/leonardo/palmeiro/kirolak/kirolak.error.log
