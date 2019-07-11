
-->Alice and Bob generates random number.

-->Alice and Bob both send the hash of the random number to each other.

-->Alice and Bob both send the random number to each other.

-->Alice and Bob verify if the received random number and its hash match or not to verify the parties honesty.

-->Using bitwise XOR, Alice and Bob calculate the Nonce,



-->Initial Data Preprocessing

-->Implemented a simple parser to input the data from Alice and Bob's datafiles into 2 Hashmaps .

	Map <String, SortedMap <Integer,String>> locGene;	// Stores Chromosome as Key and <Location,Genotype> in the SortedMap
	Map <String, SortedMap <Integer,String>> locRsid;	// Stores Chromosome as Key and <Location,RSID> in the SortedMap
	
-->T1 value is fixed and is initialized in constructor.



***Alice and Bob's Datafiles are identical to each other.


-->Divide each chromosomes into number of frames of size t1.

-->level1Frames are stored in a Linked Hashmap to preserve the order of insertion of frames.

	LinkedHashMap <String, String> level1Frames;	 	// Level1 Frame structure <Concatenation of Chromosome + Start + End+ 															   locations and RSIDs,HashedValue>
															// Using Linked Hashmap so that the order of creating Frames is preserved.

-->Only Homozygtes are kept in the substring to calculate the hashcodes.

-->Hashing method written to hash the substring using SHA-256 and nonce.

	public  String getSHAWitnNonce(String input, int nonce1 )
	

	
	
-->DNA matching method (very primitive one) written to match two level1Frames DS.
	
	public void DNAMatch(LinkedHashMap <String, String> party)