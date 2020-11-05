#!/bin/sh

#create different parts of html template for easy formatting
html='<!DOCTYPE html><html><meta charset="utf-8"><head><style>body{background-color: darkred;}h3{color: green; background-color: black; text-decoration: underline;}h4{color: lightgreen}p{color: yellow;}</style><title>Test Cases</title></head><body>'

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
currentTest="<h3>Test Num: $testNum</h3><h4>$req</h4><p><br>Method tested: $meth<br>Inputs: $inputs<br>Expected Output: $oracle<br> Actual Output: $output<br></p>"
html="$html$currentTest<br>"

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
echo "$html</body></html>" > report.html

xdg-open report.html
cat ../reports/report.html
#cat ../reports/report.txt
