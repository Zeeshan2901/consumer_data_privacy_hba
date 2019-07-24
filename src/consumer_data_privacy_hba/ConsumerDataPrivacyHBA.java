package consumer_data_privacy_hba;

import java.io.BufferedReader;
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
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom; 


/**
 * @author Zeeshan
 *
 */


public class ConsumerDataPrivacyHBA {
	
	// Stores Chromosome as Key and <Location,Genotype> in the SM
	Map <Integer, SortedMap <Integer,String>> locGene;
	// Stores Chromosome as Key and <Location,RSID> in the SM
	Map <Integer, SortedMap <Integer,String>> locRsid;
	
	//Frame fields are stored in a LinkedHashMap <Integer, SortedSet<FrameData>> 
	//where the Key is the integer and
	//value is Sorted Set of Frame Data objects (custom objects)
	LinkedHashMap <Integer, SortedSet<FrameData>> level1Frame;
	LinkedHashMap <Integer, SortedSet<FrameData>> matchingFrames;
	String alice, bob;
	//Size of Frame
	int t1;		
	//Nonce fields
	long my_nonce, party_nonce, nonce;
	String hashOfMyNonce, hashOfPartyNonce;
	
	public ConsumerDataPrivacyHBA() {
		alice="";
		bob="";
		locGene = new HashMap<Integer, SortedMap <Integer,String>>();
		locRsid = new HashMap<Integer, SortedMap <Integer,String>>();
		
		level1Frame = new LinkedHashMap<Integer, SortedSet<FrameData>>();
		matchingFrames = new LinkedHashMap<Integer, SortedSet<FrameData>>();
		
		t1=700;
	}
	
	//Method to find if a location is present and is homozygous or not.
	public boolean findLocationInMaps(int chromosome, int location, Map <Integer, SortedMap <Integer,String>> gene) {		
		return  (isHomozygous((gene.get(chromosome).get(location)))) ;
	}
	
	
	//Method to remove Special Character "--" as a Genotype in the DataFiles
	public void removeSpecial(Map<Integer, SortedMap <Integer,String>> party) {
		//int c=0;
		for(Map.Entry<Integer, SortedMap<Integer, String>> entry : locGene.entrySet()) {
			int chromo=entry.getKey();
			SortedMap<Integer, String> temp = entry.getValue();
			Set<Entry<Integer, String>> sm =temp.entrySet();
			Iterator<Entry<Integer, String>> i=sm.iterator();
			while (i.hasNext()) 
	        { 
				Map.Entry<Integer, String> m = (Map.Entry<Integer, String>)i.next();
				int key=m.getKey();
				String value=m.getValue();
				if (value.contentEquals("--")) {
					//c++;
					//remove from current object
					i.remove();
					//remove from the other party object
					party.get(chromo).remove(key);
					//System.out.println("\n Count : "+c+" || Chromosome : "+chromo+" || Location : "+key+" || Gene : "+value);
				}
	        }
			
		}
		//System.out.println("No. of Spc char removed : "+c);
	}
	
	public boolean someMatch(FrameData my, FrameData party) {
		return (my.start==party.start && my.end==party.end && my.hashValue.contentEquals(party.hashValue)) ? true : false;		
	}
	
	public void DNAMatchUsingCustomObjects(LinkedHashMap <Integer, SortedSet<FrameData>> party) {
		int count=0, chromosome=0, aliceSize=0,bobSize=0 ;
		
		//System.out.println("\n\n\n\t\t\t****FRAME MATCH RESULTS****");
		for(Map.Entry<Integer, SortedSet<FrameData>> entry : level1Frame.entrySet()) {
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
				while(it.hasNext()) {
					FrameData objB=(FrameData)it.next();
					if (someMatch(objA,objB)) {
						FrameData matchingObject= new FrameData(objA);
						matchingSet.add(matchingObject);
						count++;
						break;
					}
				}
			}
		matchingFrames.put(chromosome,matchingSet);
		//System.out.println("\n At Chromosome "+chromosome);
		//System.out.println("\t\t No. of  Alice's Frames are "+aliceSize);
		//System.out.println("\t\t No. of   Bob's  Frames are "+bobSize);
		//System.out.println("\t\t No. of matching Frames are "+count);
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
		
		//outer map iterator to traverse genotype data of each chromosome
		for(Map.Entry<Integer, SortedMap<Integer, String>> entry : locGene.entrySet()) {		
			chromosome=(entry.getKey());
			SortedSet<FrameData> set = new TreeSet<FrameData>();
			// SortedMap Iterator containing location and genotype
			SortedMap<Integer, String> temp = entry.getValue(); 
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
				// for each location increasing the counter
	            counter++;				   
	            // capture the genotype value
	            String value = (String) m.getValue(); 		
	            
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
					
					FrameData obj=new FrameData(start,startRsid,end,endRsid,getSHAWitnNonce(substring.toString(),nonce));
					set.add(obj);
					substring.delete(0, substring.length());
				}
	        } 
			level1Frame.put(chromosome,set);
		}	
		//displaySet(level1FRAMES);
	}
	 
	//Method to check if the chromsome is between 1-22, rest are ignored
	public boolean isParsable(String s) {
		if  (Character.isDigit(s.charAt(0))) {
			if(Integer.parseInt(s) <= 22)
				return true;
		}
		return false;
			
	}

	//Data File Parser
	public void readFile(String location) {
				
		String line ="";
		try {
			FileReader fr = new FileReader(location);
			BufferedReader bf =new BufferedReader(fr);
			while ((line = bf.readLine()) != null) {
				String[] row=line.split("\t");
				// Read chromosomes only between 1 and 22
				if (isParsable(row[1])){
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
			bf.close();
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
	public long generateRandom() {
		return my_nonce =ThreadLocalRandom.current().nextLong(100000,32671234);
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
	public long sendRandom() {
		return my_nonce;
	}


    // since Java ints are only 32 bits, evil Bob could take Alice's declared
    //  hash-of-nonce and by brute force determine what her nonce-contribution value is going
    // to be, and thereby be able to determine his own nonce-contribution so as to force
    // the nonce to a previously-used value.   A naive brute force attack like this would
    // not work for 64-bit longs, though maybe some more sophisticated approach would.  For
    // the short term, I recommend that you just make  nonces "long" instead of "int".


    
	//method to store other parties nonce
	public void getNonce(long n) {
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
	public  String getSHAWitnNonce(String input, long nonce1 ){ 
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
	
	
	
}



























