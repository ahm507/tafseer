#!/usr/bin/env bash


echo "Copy the database file: "
echo


cp ./books.sqlite ./app/src/main/assets/books.sqlite

echo "Now rebuilding debug APK"

gradle clean
gradle assembleDebug

cp ./app/build/outputs/apk/app-debug.apk ./app-debug.apk

echo "The compile file is at:  ./app-debug.apk"

