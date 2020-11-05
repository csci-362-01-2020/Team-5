#!/bin/sh

#create different parts of html template for easy formatting
html="<!DOCTYPE html>
<html><meta charset="utf-8"><head>
<style>body{background-color: darkred; color: lightgreen} th{color: black; text-decoration: underline;} table, th, td{text-align: center; vertical-align: middle; width: 50%; border-collapse: collapse; border: 2px solid black;}</style>
<title>Test Cases</title></head><body><table><h3><tr><th>Test Num</th><th>Req</th><th>Method Tested</th><th>Inputs</th><th>Expected Outputs</th><th>Actual Output</th></h3></tr>"

#iterate through each testCase file
for testCase in ../testCases/*
do

echo "$testCase"

IFS=$'\n';
arr=()
while read line; do
	arr+=($line);
done < "$testCase"


testNum=${arr[0]}
req=${arr[1]}
meth=${arr[2]}
inputs=${arr[3]}
oracle=${arr[4]}
outFile=${arr[5]}
testExec=${arr[6]}

space=" "


pathToTemp="../temp/"
outputFile=$pathToTemp$outFile
args=$inputs$space$oracle$space$outputFile
testJava="$testExec.java"


cd ../testCaseExecutables

#javac -d . FloatingPointFormatter.java OptionalDouble.java TestAdd.java

javac -d . FloatingPointFormatter.java OptionalDouble.java "$testJava"

java org.miradi.utils."$testExec" "$args"



#echo "Current dir: $PWD"

read -r output<$outputFile

#output=$(head -n 1 outputFile)
#output=$out

#adds formatted version of the test output to the html file
html="$html<tr><td>$testNum</td><td>$req</td><td>$meth</td><td>$inputs</td><td>$oracle</td><td>$output</td></tr>"
#html= "$html$currentTest"

#previous code for creating a text file
#echo "Test Num: $testNum
#$req
#Method tested: $meth
#Inputs: $inputs
#Expected Output: $oracle
#Actual Output: $output
#" >> ../reports/report.txt

done
#adds closing html tag and stores it all in report.html
echo "$html</table></body></html>" > report.html

xdg-open report.html
cat ../reports/report.html
#cat ../reports/report.txt
