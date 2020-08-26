
--> Nonce Creation 

-->Alice and Bob generates random number.
-->Alice and Bob both send the hash of the random number to each other.
-->Alice and Bob both send the random number to each other.
-->Alice and Bob verify if the received random number and its hash match or not to verify the parties honesty.
-->Using bitwise XOR, Alice and Bob calculate the Nonce,



--Initial Data Preprocessing

-->Implemented a simple parser to input the data from Alice and Bob's data files into Array List of Custom Objects .
-->Inputs only chromosomes from 1 to 22

	ArrayList<GenotypedData>[] genes;
	genes  = new ArrayList[CHROMOSOME_COUNT+1]; 
		for (int i=1; i<=CHROMOSOME_COUNT; i++) 
			genes[i]= new ArrayList<GenotypedData>();
	
-->T1 value is fixed and is initialized in constructor (700 as of now).
-->Number of Overlaps (n) is dynamic for the code and is initialized in constructor (4 as of now).



***Using Manuel Corpas data for testing.


-->Divide each chromosomes into number of frames of size t1. 
-->n level overlapping implemented .
-->Frames are stored in a Array List of Custom Objects.

	ArrayList<FrameData>[] frames;
	frames  = new ArrayList[CHROMOSOME_COUNT+1]; 
		for (int i=1; i<=CHROMOSOME_COUNT; i++) 
			frames[i]= new ArrayList<FrameData>();

-->Only Homozygtes and locations which are common in both files are kept in the substring to calculate the hashcodes.
-->Implemented the logic to handle one genotyping error.

	if (obj.gene1==obj.gene2 && ifLocationExistsinOtherParty(i,j,obj,locGene)) {
		if(counter%2==0) {
			evenSubstring.append(String.valueOf(obj.gene1));
			evenSubstring.append(String.valueOf(obj.gene2));
		}
		else {
			oddSubstring.append(String.valueOf(obj.gene1));
			oddSubstring.append(String.valueOf(obj.gene2));
		}
	}

-->"--" Genotype are removed from datafiles and not considered for matching
-->Hashing method written to hash the substring using SHA-256 and nonce.

	public  String getSHAWitnNonce(String input, int nonce1 )
	

	
	
-->Frame matching method is implemented.
-->It compares the custom objects to match Frames.
	
	public void frameMatch(ArrayList<FrameData>[] current,
			ArrayList<FrameData>[]party) {
	
	
-->The Frame structure can be displayed using the method

	for (int i=1;i<=22;i++) 
			for(int j=0; j< b.frames[i].size(); j++) {
				FrameData obj=(FrameData) b.frames[i].get(j);
				obj.display(obj, i);
			}