# Instructions For Operating Test Automation Framework

## Framework Overview and Structure
* This automated testing framework was developed in and designed for the Ubuntu Linux Operating System, and as such, all of the 
  following information and instruction applies for use in Ubuntu Linux.
* This framework tests five methods from the OptionalDouble and IgnoreCaseStringComparator classes of the open source Miradi project.
* The TestAutomation directory is the top-level directory of the framework and contains all components of the framework
* runAllTests.sh , a bash script found in the /scripts/ directory, handles automated testing.
* All test cases are specified in the /testCases/ directory according to the specified format.
* The /testExecutables/ directory contains the source code for the classes we are testing, any dependencies of those classes,
  as well as the source code used to test each method. It also contains the compiled *.class files for all of these.
* In order to display our knowledge of software faults, as well as the capabilities of our automated testing framework,
  five faults have been inserted to make some, but not all, of our test cases fail. A detailed specification of these faults, and how
  to remove or re-insert them can be found later in this README. 
* By default, the faults are initially included in the framework.

## Setup
* First, clone our Team-5 repository from github onto your Linux machine or virtual machine.
* To do so, change into the directory you want this repository to appear and issue the following command-line command:

  `git clone https://github.com/csci-362-01-2020/Team-5.git`

## Running Tests
* Once you have setup the framework, change directories into the TestAutomation directory.
* From there, you can run all tests by issuing the command:

  `bash scripts/runAllTests.sh`
* Upon completion of the script, a browser window will open allowing you to see all test results. 

## Handling Faults
* By default, the framework will test the methods with faults inserted.
* A specification of these faults are as follows: 

![](https://github.com/csci-362-01-2020/Team-5/blob/master/Old_Stuff/TableHW.png)

* To remove these faults, you will need to edit the OptionalDouble.java and IgnoreCaseStringComparator.java files
* These files can both be found in the /testExecutables/ directory.
* Go to the method of the fault you want to remove. 
* You will notice that both the original and faulty code still exist and are specified by comments; 
  however, the original code is surround by a multi-line comment.
* To remove a fault, remove the multi-line comment from around the original code and surround the faulty code 
  with a multi-line comment. 
* To re-insert the fault, remove the multi-line comment from around the faulty code and surround the original code 
  with a multi-line comment.
* After saving the edited file, the next time you run `bash scripts/runAllTests.sh` from the TestAutomation
  directory, your changes will be reflected in the results. 
