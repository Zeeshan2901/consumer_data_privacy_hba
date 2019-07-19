
-- Nonce Creation 
-->Alice and Bob generates random number.

-->Alice and Bob both send the hash of the random number to each other.

-->Alice and Bob both send the random number to each other.

-->Alice and Bob verify if the received random number and its hash match or not to verify the parties honesty.

-->Using bitwise XOR, Alice and Bob calculate the Nonce,



--Initial Data Preprocessing

-->Implemented a simple parser to input the data from Alice and Bob's datafiles into 2 Hashmaps .
	-->Inputs only chromosomes from 1 to 22

	Map <String, SortedMap <Integer,String>> locGene;	
	// Stores Chromosome as Key and <Location,Genotype> in the SortedMap
	Map <String, SortedMap <Integer,String>> locRsid;
	// Stores Chromosome as Key and <Location,RSID> in the SortedMap
	
-->T1 value is fixed and is initialized in constructor (700 as of now).



***Using Manuel Corpas data for testing.


-->Divide each chromosomes into number of frames of size t1. 
	-->No overlapping implemented yet.

-->level1Frames are stored in a Linked Hashmap to preserve the order of insertion of frames.

	//Frame fields are stored in a LinkedHashMap <Integer, SortedSet<FrameData>> 
	//where the Key is the integer and
	//value is Sorted Set of Frame Data objects (custom objects)
	LinkedHashMap <Integer, SortedSet<FrameData>> level1FRAMES;

-->Only Homozygtes and locations which are common in both files are kept in the substring to calculate the hashcodes.
-->"--" Genotype are removed from datafiles and not considered for matching

-->Hashing method written to hash the substring using SHA-256 and nonce.

	public  String getSHAWitnNonce(String input, int nonce1 )
	

	
	
-->DNA matching method is written to match two level1Frames DS.
	-->It compares the custom objects to match Frames.
	
	public void DNAMatchUsingCustomObjects(LinkedHashMap <Integer, SortedSet<FrameData>> party)
	
	
-->The Frame structure can be displayed using the method

	public void displaySet(LinkedHashMap <Integer, SortedSet<FrameData>> map)