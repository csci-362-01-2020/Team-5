#!/bin/bash

#create different parts of html template for easy formatting
#htmlOld="<!DOCTYPE html><html><meta charset="utf-8"><head><style>body{background-color: darkred; color: lightgreen} th{color: black; text-decoration: underline;} table, th, td{text-align: center; vertical-align: middle; width: 50%; border-collapse: collapse; border: 2px solid black;}</style><title>Test Cases</title></head><body><table><h3><tr><th>Test Num</th><th>Req</th><th>Method Tested</th><th>Inputs</th><th>Expected Outputs</th><th>Actual Output</th></h3></tr>"

#html="<!DOCTYPE html><html><meta charset="utf-8"><head><style>body{background-color: lightBlue; color: black; font-family: Courier, monospace;} th{color: black; text-decoration: underline;} table, th, td{text-align: center; vertical-align: middle; width: 50%; border-collapse: collapse; border: 2px solid black; padding: .5em} table{margin: auto;}</style><title>Test Cases</title></head><body><table><h3><tr><th>Test Num</th><th>Req</th><th>Method Tested</th><th>Inputs</th><th>Expected Outputs</th><th>Actual Output</th><th>Test Result</th></h3></tr>"

#main html
html="<!DOCTYPE html>
<html><meta charset="utf-8"><style>body{background-color: black; color: white; font-family: Courier, monospace;} a{color: white}</style><title>Test Cases</title><nav><ul>"


#each test case is put in the same temporary string html and transfered
htmlT="<!DOCTYPE html><html><meta charset="utf-8"><head>"

count=1

cd ..
cd ./testCaseExecutables
for testCase in ../testCases/*
do
html=$html"<li><a href="../reports/report$count.html">Test Case: $count</a></li>"
count=$((count+1))
done

#makes html files for number of test cases

html=$html"</ul></nav><head>"


# compile dependencies
#cd ..
#cd ./testCaseExecutables
#javac -Djava.ext.dirs=./testCaseExecutables
#javac -d "testCaseExecutables" #FloatingPointFormatter.java OptionalDouble.java IgnoreCaseStringComparator.java
count=1

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
#outFile=${arr[5]}
testExec=${arr[5]}

space=" "

args=$inputs$space$oracle$space$outputFile

#testJava="$testExec.java"


#compile and run test executable
#javac -d 

#reads test executable output into variable in format "{Actual Output};{Test Result}"
{ read javaOut; }< <(java org.miradi.utils."$testExec" "$args")


#parses output and test result 
IFS=';'
read -a outputArray <<< "$javaOut"
output=${outputArray[0]}
pass=${outputArray[1]}


##adds formatted version of the test output to the html file
#html="$html<tr><td>$testNum</td><td>$req</td><td>$meth</td><td>$inputs</td><td>$oracle</td><td>$output</td><td>$pass</td></tr>"
##"$html</table></body></html>" > ../reports/report"$count".html
#html=$htmlTemp

#adds formatted version of the test output to the html file
htmlT="$htmlT<title>Test Case"$count"</title><style>body{background-color: lightBlue; color: black; font-family: Courier, monospace;} th{color: black; text-decoration: underline;} table, th, td{text-align: center; vertical-align: middle; width: 50%; border-collapse: collapse; border: 2px solid black; padding: .5em} table{margin: auto;}</style><title>Test Cases</title></head><body><table><h3><tr><th>Test Num</th><th>Req</th><th>Method Tested</th><th>Inputs</th><th>Expected Outputs</th><th>Actual Output</th><th>Test Result</th></h3></tr><tr><td>$testNum</td><td>$req</td><td>$meth</td><td>$inputs</td><td>$oracle</td><td>$output</td><td>$pass</td></tr>"
echo "$htmlT</table></body></html>" > ../reports/report"$count".html

htmlT="<!DOCTYPE html><html><meta charset="utf-8"><head>"

count=$((count+1))
done
#adds closing html tag and stores it all in report.html
echo "$html</table></body></html>" > ../reports/report.html

xdg-open ../reports/report.html 
