package consumer_data_privacy_hba;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet; 


/**
 * @author Zeeshan
 *
 */


public class ConsumerDataPrivacyHBA {
	
	Map <Integer, SortedMap <Integer,String>> locGene;	// Stores Chromosome as Key and <Location,Genotype> in the SM
	Map <Integer, SortedMap <Integer,String>> locRsid;	// Stores Chromosome as Key and <Location,RSID> in the SM
	LinkedHashMap <String, String> level1Frames;	 	// Level1 Frame structure <Concatenation of Chromosome + Start + End+ locations and RSIDs, HashedValue>
													 	// Using Linked Hashmap so that the order of creating Frames is preserved.
	LinkedHashMap <String, String> match;
	
	//Frame fields are stored in a LinkedHashMap <Integer, SortedSet<FrameData>> 
	//where the Key is the integer and
	//value is Sorted Set of Frame Data objects (custom objects)
	LinkedHashMap <Integer, SortedSet<FrameData>> level1FRAMES;
	LinkedHashMap <Integer, SortedSet<FrameData>> matchingFrames;
	String alice, bob;
	int t1;												//Size of Frame
	int my_nonce, party_nonce, nonce;
	String hashOfMyNonce, hashOfPartyNonce;
	
	public ConsumerDataPrivacyHBA() {
		alice="";
		bob="";
		locGene = new HashMap<Integer, SortedMap <Integer,String>>();
		locRsid = new HashMap<Integer, SortedMap <Integer,String>>();
		level1Frames = new LinkedHashMap<String, String>();
		level1FRAMES = new LinkedHashMap<Integer, SortedSet<FrameData>>();
		matchingFrames = new LinkedHashMap<Integer, SortedSet<FrameData>>();
		match = new LinkedHashMap<String, String>();
		t1=700;
	}
	
	//Method to find if a location is present and is homozygous or not.
	public boolean findLocationInMaps(int chromosome, int location, Map <Integer, SortedMap <Integer,String>> gene) {		
		return  (isHomozygous((gene.get(chromosome).get(location)))) ;
	}
	
	
	//Method to remove Special Character "--" as a Genotype in the DataFiles
	public void removeSpecial(Map<Integer, SortedMap <Integer,String>> party) {
		int c=0;
		for(Map.Entry<Integer, SortedMap<Integer, String>> entry : locGene.entrySet()) {
			int chromo=entry.getKey();
			SortedMap<Integer, String> temp = entry.getValue(); // SortedMap Iterator containing location and genotype
			Set<Entry<Integer, String>> sm =temp.entrySet();
			Iterator<Entry<Integer, String>> i=sm.iterator();
			while (i.hasNext()) 
	        { 
				Map.Entry<Integer, String> m = (Map.Entry<Integer, String>)i.next();
				int key=m.getKey();
				String value=m.getValue();
				if (value.contentEquals("--")) {
					c++;
					i.remove();		//remove from current object
					@SuppressWarnings("unused")
					String s1=party.get(chromo).remove(key);		//remove from the other party object
					//System.out.println("\n Count : "+c+" || Chromosome : "+chromo+" || Location : "+key+" || Gene : "+value);
				}
	        }
			
		}
		System.out.println("No. of Spc char removed : "+c);
	}
	
	public boolean someMatch(FrameData my, FrameData party) {
		return (my.start==party.start && my.end==party.end && my.hashValue.contentEquals(party.hashValue)) ? true : false;		
	}
	
	public void DNAMatchUsingCustomObjects(LinkedHashMap <Integer, SortedSet<FrameData>> party) {
		int count=0, chromosome=0, aliceSize=0,bobSize=0 ;
		
		System.out.println("\n\n\n\t\t\t****FRAME MATCH RESULTS****");
		for(Map.Entry<Integer, SortedSet<FrameData>> entry : level1FRAMES.entrySet()) {
			count=aliceSize=bobSize=0;
			chromosome=(entry.getKey());
			SortedSet<FrameData> matchingSet = new TreeSet<FrameData>();
			SortedSet<FrameData> set=entry.getValue();
			Iterator<FrameData> i=set.iterator();
			aliceSize=set.size();
			while (i.hasNext()) {
				FrameData objA=(FrameData)i.next();
				SortedSet<FrameData> partySet=party.get(chromosome);
				Iterator<FrameData> it=partySet.iterator();
				bobSize=partySet.size();
				//System.out.println("\n At Chromosome "+chromosome+" the no. of Alice's Frames are "+set.size()+" and the no. of Bob's Frames are "+partySet.size());
				while(it.hasNext()) {
					FrameData objB=(FrameData)it.next();
					if (someMatch(objA,objB)) {
						FrameData matchingObject= new FrameData(objA);
						matchingSet.add(matchingObject);
						count++;
					}
				}
			}
		matchingFrames.put(chromosome,matchingSet);
		System.out.println("\n At Chromosome "+chromosome);
		System.out.println("\t\t No. of  Alice's Frames are "+aliceSize);
		System.out.println("\t\t No. of   Bob's  Frames are "+bobSize);
		System.out.println("\t\t No. of matching Frames are "+count);
		}
		
	}
	
	public void displaySet(LinkedHashMap <Integer, SortedSet<FrameData>> map) {
		
		for(Map.Entry<Integer, SortedSet<FrameData>> entry : map.entrySet()) {
			int chromosome=(entry.getKey());
			SortedSet<FrameData> set=entry.getValue();
			Iterator<FrameData> i=set.iterator();
			while (i.hasNext()) {
				FrameData obj=(FrameData)i.next();
				obj.display(obj,chromosome);
			}
		}
		
	}
	
	
	//Method to divide the chromosomes into Frames of size T1
	public void implementFrames(Map <Integer, SortedMap <Integer,String>> gene) {
		
		int chromosome, location;
		
		
		for(Map.Entry<Integer, SortedMap<Integer, String>> entry : locGene.entrySet()) {		//outer map iterator to traverse genotype data of each chromosome
			chromosome=(entry.getKey());
			SortedSet<FrameData> set = new TreeSet<FrameData>();
			SortedMap<Integer, String> temp = entry.getValue(); // SortedMap Iterator containing location and genotype
			Set<Entry<Integer, String>> sm =temp.entrySet();
			Iterator<Entry<Integer, String>> i=sm.iterator();
			int counter=0,start=0,end=0;
			StringBuilder substring= new StringBuilder("");
			while (i.hasNext()) 
	        { 
				
				Map.Entry<Integer, String> m = (Map.Entry<Integer, String>)i.next();
				
				location=(Integer) m.getKey();
				
				// Beginning of Frame to capture start location and initialize the substing to empty
				if (counter==0) {		
					start=(Integer) m.getKey();
					substring.delete(0, substring.length());
				}  
	            counter++;				// for each location increasing the counter          
	            String value = (String) m.getValue(); 		// capture the genotype value
	            
	            // form a substring only if its homozygous and the locations match in both parties
	            if (isHomozygous(value)  && findLocationInMaps(chromosome,location,gene)) // && findLocationInMaps(chromosome,location,gene)					
	            	substring.append(value);
	            		
	          //end of frame, capture end location and rsid to form the Frame Linked hashmap 
	            if (counter==t1) {			
					end=(Integer) m.getKey();
					counter=0;                    
					String startRsid=findRsid(chromosome,start);
					String endRsid=findRsid(chromosome,end);

                    // OFK: eventual security issue: we don't want to use frames whose substrings are too short (have to set threshold)
                    // because someone could try to determine the substring from the SHA by brute force
					// Zee: Will work on that(T1 is set for 700)
					//System.out.println("\n Chromosome : "+chromosome+" || Start : "+start+" || RSID : "+startRsid+" || End : "+end+" || RSID : "+endRsid+" || String of Alleles : " +substring+" || Hashed Value : "+getSHA(substring.toString()));
					level1Frames.put(String.valueOf(chromosome)+"#"+String.valueOf(start)+"#"+startRsid+"#"+String.valueOf(end)+"#"+endRsid, getSHAWitnNonce(substring.toString(),nonce));
					FrameData obj=new FrameData(start,startRsid,end,endRsid,getSHAWitnNonce(substring.toString(),nonce));
					set.add(obj);
					substring.delete(0, substring.length());
				}
	        } 
			level1FRAMES.put(chromosome,set);
		}	
		//displaySet(level1FRAMES);
	}
	 
	
	public boolean isNumeric(String s) {
		return (Character.isDigit(s.charAt(0))) ? true:false;
			
	}

	//Data File Parser
	public void readFile(String location) {
				
		String line ="";
		try {
			FileReader fr = new FileReader(location);
			Scanner sc =new Scanner(fr);
			while (sc.hasNextLine()) {
				line=sc.nextLine();
				String[] row=line.split("\t");
				
				// Read chromosomes only between 1 and 22
				if (isNumeric(row[1]) && Integer.parseInt(row[1]) <= 22){
					if ( locGene.containsKey(Integer.parseInt(row[1])) ) {
						locGene.get(Integer.parseInt(row[1])).put(Integer.parseInt(row[2]),row[3]);
						locRsid.get(Integer.parseInt(row[1])).put(Integer.parseInt(row[2]),row[0]);
					}
					else{
						SortedMap <Integer,String> sm=new TreeMap<Integer,String>();
						SortedMap <Integer,String> sm1=new TreeMap<Integer,String>();
						sm.put(Integer.parseInt(row[2]),row[3]);
						sm1.put(Integer.parseInt(row[2]),row[0]);
						locGene.put(Integer.parseInt(row[1]),sm);	
						locRsid.put(Integer.parseInt(row[1]),sm1);
					}			
				}
			}
			sc.close();
		}catch(IOException e) {
		System.out.println("Exception in reading at "+location);
		e.printStackTrace();
	}	
	}
	
	//Method to check is an allele is Homozygous or not
	public boolean isHomozygous(String allele) {
		return (allele.charAt(0)==allele.charAt(1));
	}
	
	//Method to find RSID given the chromosome and location
    // OFK: why not something like locRsid.get(""+key).get(location) to gain full benefit of top-level Map
    // Zee : Fixed
	public String findRsid(int key, int location) {
		return locRsid.get(key).get(location);
	}
	
	//Method to find Genotype given the chromosome and location
    // OFK: same issue as findRsid
    // Zee: Fixed
	public String findGenotype(int key, int location) {
		/*
		 * for(Map.Entry<String, SortedMap<Integer, String>> entry : locGene.entrySet())
		 * { if (Integer.parseInt(entry.getKey())==key) { SortedMap<Integer, String>
		 * temp = entry.getValue(); return temp.get(location); } } return null;
		 */
		return locGene.get(key).get(location);
	}
	
	
	
	//Method to generate a Random number
	public int generateRandom() {
		Random rand = new Random();
		return my_nonce= rand.nextInt((32671234 - 100000) + 1) + 100000;
	}
	
	//Method to send Hash of random number to the other party
	public String sendHash() {
		return(hashOfMyNonce=getSHA(String.valueOf(generateRandom())));
	}
	
	//Method to verify if Hash of Random matches with the random number sent by other party
	public boolean verifyHashNonce() {
		return hashOfPartyNonce.contentEquals(getSHA(String.valueOf(party_nonce)));
	}
	
	//Method to send Random number to the other party
	public int sendRandom() {
		return my_nonce;
	}


    // since Java ints are only 32 bits, evil Bob could take Alice's declared
    //  hash-of-nonce and by brute force determine what her nonce-contribution value is going
    // to be, and thereby be able to determine his own nonce-contribution so as to force
    // the nonce to a previously-used value.   A naive brute force attack like this would
    // not work for 64-bit longs, though maybe some more sophisticated approach would.  For
    // the short term, I recommend that you just make  nonces "long" instead of "int".


    
	//method to store other parties nonce
	public void getNonce(int n) {
		party_nonce=n;
	}
	
	//get  hash of other parties nonce
	public void getHashofNonce(String s) {
		hashOfPartyNonce=s;
	}
	
	//Display the NONCE fields
	public void displayNonce() {
		System.out.println("\n My Data		: "+my_nonce+" :: "+hashOfMyNonce);
		System.out.println("\n Party Data	: "+party_nonce+" :: "+hashOfPartyNonce);
	}
	
	//Method to Calculate the Ultimate NONCE
	public void caluclateNonce() {
		nonce=my_nonce^party_nonce;
		//System.out.println("\n Nonce : " +nonce);
	}
	
	//Method to generate Hash with nonce
	public  String getSHAWitnNonce(String input, int nonce1 ){ 
        try { 
  
            // Static getInstance method is called with hashing SHA 
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
  
            
            //update the digest with nonce
            md.update(String.valueOf(nonce1).getBytes());
            // digest() method called 
            // to calculate message digest of an input 
            // and return array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into sign-magnitude  representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
  
            return hashtext; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            System.out.println("Exception thrown"
                               + " for incorrect algorithm: " + e); 
  
            return null; 
        } 
    } 
	
	//Method to generate Hash w/o nonce
	public  String getSHA(String input ){ 
        try { 
  
            // Static getInstance method is called with hashing SHA 
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
  
            // digest() method called 
            // to calculate message digest of an input 
            // and return array of byte 
            byte[] messageDigest = md.digest(input.getBytes()); 
  
            // Convert byte array into sign-magnitude  representation 
            BigInteger no = new BigInteger(1, messageDigest); 
  
            // Convert message digest into hex value 
            String hashtext = no.toString(16); 
  
            while (hashtext.length() < 32) { 
                hashtext = "0" + hashtext; 
            } 
  
            return hashtext; 
        } 
  
        // For specifying wrong message digest algorithms 
        catch (NoSuchAlgorithmException e) { 
            System.out.println("Exception thrown"
                               + " for incorrect algorithm: " + e); 
  
            return null; 
        } 
    } 
	
	/*
	 * public void putInClass(String file) {
	 * 
	 * Map <String, SNPInfo> dna= new HashMap<String, SNPInfo>(); String line ="";
	 * try { FileReader fr = new FileReader(file); Scanner sc =new Scanner(fr);
	 * while (sc.hasNextLine()) { line=sc.nextLine(); String[] row=line.split("\t");
	 * 
	 * SNPInfo snpData= new SNPInfo(row[0],row[2],row[3]); if (isHomozygous(row[3]))
	 * { dna.put(row[1],snpData); } } sc.close();
	 * System.out.println("\nNew Map: \n"+ dna);
	 * 
	 * }catch(IOException e) { System.out.println("Exception in reading at "+file);
	 * e.printStackTrace(); } }
	 */
	
	 /*public void hashMatch() {
 	
 	//System.out.println("\nHashCode Generated by SHA-256 for:"); 
    //System.out.println("\n Alice: " + alice+ getSHA(alice)); 
	    //System.out.println("\n Bob  : " + bob + getSHA(bob)); 
	    
	    if (alice.equals(bob))
	    	System.out.println("\n Alice and Bob Matches");
	    else
	    	System.out.println("\n Alice and Bob Don't Match");    	
 }



public void readFromAlice(){
	try {
			FileReader fr = new FileReader("input/alice.txt");
			Scanner sc =new Scanner(fr);
			while (sc.hasNextLine()) {
				alice=sc.nextLine();
			}
			fr.close();
			sc.close();
	}catch(IOException e) {
		System.out.println("Exception in reading from Alice");
		e.printStackTrace();
	}
	
}



public void readFromBob() {
	
	try {
		FileReader fr = new FileReader("input/bob.txt");
		Scanner sc =new Scanner(fr);
		while (sc.hasNextLine()) {
			bob=sc.nextLine();
		}
		fr.close();
		sc.close();
	}catch(IOException e) {
	System.out.println("Exception in reading from Bob");
	e.printStackTrace();
}
	
}*/
	
	//Method to display the Frame Structure
	public void displayFrames (LinkedHashMap <String, String> frames) {
		for(Map.Entry<String, String> m:frames.entrySet()){  
			   System.out.println("\nLocation Details : " +m.getKey()+" || Hashed Text :  "+m.getValue());  
			  }   
	}
		
	//Method to check if the Hashes from the frames match
	public void hashMatch(LinkedHashMap <String, String> party) {
		
		for(Map.Entry<String, String> m:level1Frames.entrySet()){
			String key1 = m.getKey();
			for(Map.Entry<String, String> p:party.entrySet()){
				String key2=p.getKey();
				if (key1.contentEquals(key2)) {
					String value1 = m.getValue();
					String value2 = p.getValue();
					if (value1.contentEquals(value2))
						match.put(key1,value1);
				}
			
		}	
		}
		displayFrames(match);
	}
	
	//Method to check if the Frame boundaries match or not between parties
	public void locMatch(LinkedHashMap <String, String> party) {
	int count=0;	
	System.out.println("\n Matches in Locations\n\n");
		for(Map.Entry<String, String> m:level1Frames.entrySet()){
			String key1 = m.getKey();
			for(Map.Entry<String, String> p:party.entrySet()){
				String key2=p.getKey();
				if (key1.contentEquals(key2)) {
					System.out.println("Location : " +key1);
					count++;
				}
		}	
		}
		
		System.out.println("\n Size of T1   Frame : " +t1);
		System.out.println("\n No. of Alice's Frames : " +level1Frames.size());
		System.out.println("\n No. of Bob's   Frames : " +party.size());
		System.out.println("\n No. of Match   Frames : " +match.size());
		System.out.println("\n No. of Frames matches b/w Alice and Bob : " +count);
		System.out.println("\n Size of locRsid : " +locRsid.size());
		System.out.println("\n Size of locGene : " +locGene.size());
	}
	
	//Junk
	public void deleteLocationFromMaps(int chromosome, int location) {
		
		for(Map.Entry<Integer, SortedMap<Integer, String>> entry : locRsid.entrySet()){ 
			if ((entry.getKey())==chromosome) { 
				SortedMap<Integer, String> temp = entry.getValue(); 
				temp.remove(location);
				System.out.println("DOne");
				} 
			}
		for(Map.Entry<Integer, SortedMap<Integer, String>> entry1 : locGene.entrySet()){ 
			if ((entry1.getKey())==chromosome) { 
				SortedMap<Integer, String> temp1 = entry1.getValue(); 
				temp1.remove(location);
				System.out.println("DONE");
				} 
			}
	}
	
	//Junk
	public void locationMatch(Map <Integer, SortedMap <Integer,String>> gene) {
		int count=0, location;
		for(Map.Entry<Integer, SortedMap<Integer, String>> entry : locGene.entrySet()) {
			int chromosome=(entry.getKey());
			SortedMap<Integer, String> temp = entry.getValue(); 
			Set<Entry<Integer, String>> sm =temp.entrySet();
			Iterator<Entry<Integer, String>> i=sm.iterator();
			while (i.hasNext()) 
	        { 
				System.out.println("\n"+count++);
				Map.Entry<Integer, String> m = (Map.Entry<Integer, String>)i.next();
				location=(Integer) m.getKey();
				if (findLocationInMaps(chromosome,location, gene)==false)
					deleteLocationFromMaps(chromosome,location);
				System.out.println(locGene);
				System.out.println(locRsid);
	        }
		}
	}
	
	
	//Junk Method to show special characters in the DataFiles
		public void showSpecial() {
			
			int c=0;
			System.out.println("Start");
			for(Map.Entry<Integer, SortedMap<Integer, String>> entry : locGene.entrySet()) {
				int chromo=entry.getKey();
				SortedMap<Integer, String> temp = entry.getValue(); // SortedMap Iterator containing location and genotype
				Set<Entry<Integer, String>> sm =temp.entrySet();
				Iterator<Entry<Integer, String>> i=sm.iterator();
				while (i.hasNext()) 
		        { 
					Map.Entry<Integer, String> m = (Map.Entry<Integer, String>)i.next();
					int key=m.getKey();
					String value=m.getValue();
					if (value.contentEquals("--")) {
						c++;
						System.out.println("\n Count : "+c+" || Chromosome : "+chromo+" || Location : "+key+" || Gene : "+value);
					}
		        }
			}
			System.out.println("END");
		}
	
}



























