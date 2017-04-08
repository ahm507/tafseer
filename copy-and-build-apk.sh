#!/usr/bin/env bash



echo "Copy the database file "
echo


cp ./books.sqlite ./app/src/main/assets/

echo "Now rebuilding debug APK"

gradle clean
gradle assembleDebug

