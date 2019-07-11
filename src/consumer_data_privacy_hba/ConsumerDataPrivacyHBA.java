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
import java.util.TreeMap; 


/**
 * @author Zeeshan
 *
 */


public class ConsumerDataPrivacyHBA {
	
	Map <String, SortedMap <Integer,String>> locGene;	// Stores Chromosome as Key and <Location,Genotype> in the SM
	Map <String, SortedMap <Integer,String>> locRsid;	// Stores Chromosome as Key and <Location,RSID> in the SM
	LinkedHashMap <String, String> level1Frames;	 	// Level1 Frame structure <Concatenation of Chromosome + Start + End+ locations and RSIDs, HashedValue>
													 	// Using Linked Hashmap so that the order of creating Frames is preserved.
	String alice, bob;
	int t1;												//Size of Frame
	int my_nonce, party_nonce, nonce;
	String hashOfMyNonce, hashOfPartyNonce;
	
	public ConsumerDataPrivacyHBA() {
		alice="";
		bob="";
		locGene = new HashMap<String, SortedMap <Integer,String>>();
		locRsid = new HashMap<String, SortedMap <Integer,String>>();
		level1Frames = new LinkedHashMap<String, String>();
		t1=40;
	}
	
	//Method to find RSID given the chromosome and location
	public String findRsid(int key, int location) {
		for(Map.Entry<String, SortedMap<Integer, String>> entry : locRsid.entrySet()) {
			if (Integer.parseInt(entry.getKey())==key) {
				SortedMap<Integer, String> temp = entry.getValue();
				return temp.get(location);
			}
		}
		return null;
	}
	
	//Method to find Genotype given the chromosome and location
	public String findGenotype(int key, int location) {
		for(Map.Entry<String, SortedMap<Integer, String>> entry : locGene.entrySet()) {
			if (Integer.parseInt(entry.getKey())==key) {
				SortedMap<Integer, String> temp = entry.getValue();
				return temp.get(location);
			}
		}
		return null;
	}
	
	//Method to display the Frame Structure
	public void displayFrames (LinkedHashMap <String, String> frames) {
		for(Map.Entry<String, String> m:frames.entrySet()){  
			   System.out.println("\nLocation Details : " +m.getKey()+" || Hashed Text :  "+m.getValue());  
			  }   
	}
	
	//Method to generate a Random number
	public int generateRandom() {
		Random rand = new Random();
		return my_nonce= rand.nextInt((32671234 - 100000) + 1) + 100000;
	}
	
	public String sendHash() {
		return(hashOfMyNonce=getSHA(String.valueOf(generateRandom())));
	}
	
	public boolean verifyHashNonce() {
		return hashOfPartyNonce.contentEquals(getSHA(String.valueOf(party_nonce)));
	}
	
	public int sendRandom() {
		return my_nonce;
	}
	
	//method to store other parties nonce
	public void getNonce(int n) {
		party_nonce=n;
	}
	
	//get  hash of other parties nonce
	public void getHashofNonce(String s) {
		hashOfPartyNonce=s;
	}
	
	public void displayNonce() {
		System.out.println("\n My Data		: "+my_nonce+" :: "+hashOfMyNonce);
		System.out.println("\n Party Data	: "+party_nonce+" :: "+hashOfPartyNonce);
	}
	
	public void caluclateNonce() {
		nonce=my_nonce^party_nonce;
		System.out.println("\n Nonce : " +nonce);
	}
	
	
	public  String getSHAWitnNonce(String input, int nonce1 ){ 
        try { 
  
            // Static getInstance method is called with hashing SHA 
            MessageDigest md = MessageDigest.getInstance("SHA-256"); 
  
            
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
	
	//Method to divide the chromosomes into Frames of size T1
	public void implementFrames() {
		
		for(Map.Entry<String, SortedMap<Integer, String>> entry : locGene.entrySet()) {		//outer map iterator to traverse genotype data of each chromosome
			
			SortedMap<Integer, String> temp = entry.getValue(); // SortedMap Iterator containing location and genotype
			Set<Entry<Integer, String>> sm =temp.entrySet();
			Iterator<Entry<Integer, String>> i=sm.iterator();
			int counter=0,start=0,end=0;
			String substring="";
			while (i.hasNext()) 
	        { 
				Map.Entry<Integer, String> m = (Map.Entry<Integer, String>)i.next();
				if (counter==0) {		// Beginning of Frame to capture start location and initialize the substing to empty
					start=(Integer) m.getKey();
					substring="";
				}  
	            counter++;				// for each location increasing the counter          
	            String value = (String) m.getValue(); 		// capture the genotype value
	            if (isHomozygous(value))					// form a substring only if its homozygous
	            	substring+=value;
	            		// ofk: unless Java compilers have improved, in the
                    	// past one would get much better speed from using a
                    	// StringBuilder rather than "+=".
	            if (counter==t1) {			//end of frame, capture end location and rsid to form the Frame Linked hashmap 
					end=(Integer) m.getKey();
					counter=0;
					int chromosome=Integer.parseInt(entry.getKey());
					String startRsid=findRsid(chromosome,start);
					String endRsid=findRsid(chromosome,end);
					//System.out.println("\n Chromosome : "+chromosome+" || Start : "+start+" || RSID : "+startRsid+" || End : "+end+" || RSID : "+endRsid+" || String of Alleles : " +substring+" || Hashed Value : "+getSHA(substring));
					level1Frames.put(String.valueOf(chromosome)+"#"+String.valueOf(start)+"#"+startRsid+"#"+String.valueOf(end)+"#"+endRsid, getSHAWitnNonce(substring,nonce));
					substring="";
				}
	        } 
		}	
		System.out.println("\n Frames of size T1 : "+t1);
		displayFrames(level1Frames);
	}
	 
	

	
	public void readFile(String location) {
				
		String line ="";
		try {
			FileReader fr = new FileReader(location);
			Scanner sc =new Scanner(fr);
			while (sc.hasNextLine()) {
				line=sc.nextLine();
				String[] row=line.split("\t");
				
				
				if ( locGene.containsKey(row[1]) ) {
					locGene.get(row[1]).put(Integer.parseInt(row[2]),row[3]);
					locRsid.get(row[1]).put(Integer.parseInt(row[2]),row[0]);
				}
				else{
					SortedMap <Integer,String> sm=new TreeMap<Integer,String>();
					SortedMap <Integer,String> sm1=new TreeMap<Integer,String>();
					sm.put(Integer.parseInt(row[2]),row[3]);
					sm1.put(Integer.parseInt(row[2]),row[0]);
					locGene.put(row[1],sm);	
					locRsid.put(row[1],sm1);
				}			
			}			
			sc.close();
		}catch(IOException e) {
		System.out.println("Exception in reading at "+location);
		e.printStackTrace();
	}	
	}
	
	public boolean isHomozygous(String allele) {
		return (allele.charAt(0)==allele.charAt(1));
	}
	
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
	
}



























