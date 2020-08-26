# The Hashing Based Approach


Related overleaf document:
https://www.overleaf.com/project/5c2ff8405c17106274d0b564

Dependencies: Need Java 1.8

To build this project:

* Clone the project
* Compile the java source code
```
cd  src/
javac consumer_data_privacy_hba/*.java
```

**This is a Secure 2-Party Computation so this project can be executed on 2 different machines as well as on the same machine.**

* To run the project on same machine 
	* Create the jars for both User1 and User2 
	```
	jar cfm User1.jar Manifest.txt consumer_data_privacy_hba/*.class
	jar cfm User2.jar Manifest2.txt consumer_data_privacy_hba/*.class
	```
	* Execute the jars by opening 2 shells and executing each jar. 
		* Make sure User1.jar is executed before User2.jar.
	``` 	
	java -jar User1.jar "port (e.g: 5000)" "location of the input GENOTYPED datafile" "cMPerFrame(value: 5 or 25)"
	java -jar User2.jar "port (e.g: 5000)" "IP address of the Server (in this case same machine address e.g:127.0.0.1)" "location of the input GENOTYPED datafile" "cMPerFrame(value: 5 or 25)"
	```

* To run the project on different machines
	* End users should decide on the roles (Server/Client)
	* User1 is Server and User2 is client
	* User1 should share their IP address and port to bind to the User2
	* Both User1 and User2 should follow the same commands.
	* Make sure User1.jar is executed before User2.jar.


* To run multple test cases on the same machine
	
	* Modify the TestCases.java file accordingly.
	```
	/*
	 * Modify Below Variables Accordingly To Run Tests 
	 */
		String loc = "test_files/case3/";		//directory for test files for Server
		String loc1= "test_files/case3/";		//directory for test files for Server
		String csv = ".csv";				//extension of the test file
		String txt =".txt";				//extension of the test file
		String results= "input/TestCasesResults.csv";	//results are stored in this file
	/*
	 * 
	 */
	```
	* Compile TestCases.java 
	```
	cd  src/
	javac consumer_data_privacy_hba/TestCases.java
	``` 
	* Create the jar
	```
	jar cfm TestCases.jar Manifest3.txt consumer_data_privacy_hba/*.class
	```
 	* Execute
	```
	java -jar TestCases.jar
	```

