#!/bin/sh

#get proper case file
input="../testCases/testCase1.txt"

IFS=$'\n';
while read line; do
	arr+=($line);
done < "$input"


testNum=${arr[0]}
req=${arr[1]}
meth=${arr[2]}
inputs=${arr[3]}
oracle=${arr[4]}
outFile=${arr[5]}

space=" "


pathToTemp="../temp/"
outputFile=$pathToTemp$outFile
args=$inputs$space$oracle$space$outputFile


cd ../testCaseExecutables

javac -d . FloatingPointFormatter.java OptionalDouble.java TestAdd.java

java org.miradi.utils.TestAdd "$args"



#echo "Current dir: $PWD"

read -r output<$outputFile

#output=$(head -n 1 outputFile)
#output=$out

echo "Test Num: $testNum
$req
Method tested: $meth
Inputs: $inputs
Expected Output: $oracle
Actual Output: $output" >> ../reports/report.txt

#Note: since we are appending it is neccessary to delete report.txt after each run.


cat ../reports/report.txt





