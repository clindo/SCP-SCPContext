#! /bin/sh
cd `dirname "$0"`
javac clindo/contextJ/file/*.java
javac *.java
echo If there was no error, you can now execute the example program by calling \"java Demo\".
java Demo
