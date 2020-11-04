#!/bin/sh

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

echo "Test Num: $testNum
$req
Method tested: $meth
Inputs: $inputs
Expected Output: $oracle
Actual Output: $output
" >> ../reports/report.txt

done

cat ../reports/report.txt


	
