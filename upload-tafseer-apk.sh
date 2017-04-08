#!/usr/bin/env bash



echo "Upload Tafseer APK"
echo


rsync -avz -e "ssh -i SalaryControl-Amazon.pem" ./app/build/outputs/apk/app-debug.apk ubuntu@54.213.85.129:/opt/tomcat/webapps/ROOT/app-debug.apk

 
echo "download from http://www.salarycontrol.com/app-debug.apk"

