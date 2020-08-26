
## Generate Nonce

* Generate and Hash Secure Random Number
	* Alice and Bob both generate secure random number R<sub>A</sub> and R<sub>B</sub> respectively.
	* Alice and Bob both generate the hash of the secure random number h<sub>A</sub>=H(R<sub>A</sub>) and h<sub>B</sub>=H(R<sub>B</sub>) respectively.
	
* Share hashes of secure random number
	* Alice sends h<sub>A</sub> to Bob.
	* Bob sends h<sub>B</sub> to Alice.
	
* Share secure random numbers
	* Alice sends R<sub>A</sub> to Bob.
	* Bob sends R<sub>B</sub> to Alice.

* Verify random numbers
	* Alice verifies if H(R<sub>B</sub>)= h<sub>B</sub>.
	* Bob verififes if H(R<sub>A</sub>)= h<sub>A</sub>.

* Nonce creation
	* If verfifictaion for both Alice and Bob is true, then nonce = R<sub>A</sub> XOR R<sub>B</sub> 
	* If verfifictaion for both Alice and Bob is false, then abort.



## Checksum exchange

* Alice and Bob both calculate the checksum of their respective generated hashes.
* Alice divides her checksum into two halves.
* Alice sends the first half of the checksum to Bob.
* Bob sends his entire calculated checksum to Alice.
* Alice sends the second half of her checksum to Bob.



## Hash exchange

* Even and odd position strings for each frame are concatenated using a delimiter.
* A list of concatenated strings for all frames is created.
* The list is sorted, so that the parties do not get to know which string belongs to which frame.
* Batches of 100 strings from the list are shared by both the parties to each other, one at a time.
* Once all the batches are sent, both the parties will have the hashed values of each frame.
* Both parties will split the received hashed values in batches and add them to a list.
* Both parties will calculate the checksum of the received hashes and match with the checksum received at the start of the current communication.
* If the checksum match is true for both parties, then the parties appear to have followed the protocol, else one or both of the parties is malicious. 





















