package consumer_data_privacy_hba;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
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
import java.util.stream.IntStream;


/**
 * @author Zeeshan
 *
 */


public class ConsumerDataPrivacyHBA<genes> {
	
	// Stores Chromosome as Key and <Location,Genotype> in the SM
	Map <Integer, SortedMap <Integer,String>> locGene;
	// Stores Chromosome as Key and <Location,RSID> in the SM
	Map <Integer, SortedMap <Integer,String>> locRsid;
	
	//Frame fields are stored in a LinkedHashMap <Integer, SortedSet<FrameData>> 
	//where the Key is the integer and
	//value is Sorted Set of FrameData objects (custom objects)
	LinkedHashMap <Integer, SortedSet<FrameData>> level1Frame;
	LinkedHashMap <Integer, SortedSet<FrameData>> level2Frame;
	LinkedHashMap <Integer, SortedSet<FrameData>> matchingFrames;
	//Size of Frame
	int t1, n;		
	//Nonce fields
	long my_nonce, party_nonce, nonce;
	String hashOfMyNonce, hashOfPartyNonce;
	
	final static int CHROMOSOME_COUNT =22;
	
	ArrayList<GenotypedData>[] genes;
	
	
	public ConsumerDataPrivacyHBA() {
		
		locGene = new HashMap<Integer, SortedMap <Integer,String>>();
		locRsid = new HashMap<Integer, SortedMap <Integer,String>>();
		
		level1Frame = new LinkedHashMap<Integer, SortedSet<FrameData>>();
		level2Frame = new LinkedHashMap<Integer, SortedSet<FrameData>>();
		matchingFrames = new LinkedHashMap<Integer, SortedSet<FrameData>>();
		
		t1=700;
		n=2;
		
		
		genes  = new ArrayList[CHROMOSOME_COUNT+1]; 
		for (int i=1; i<=CHROMOSOME_COUNT; i++) 
			genes[i]= new ArrayList<GenotypedData>();
			
		
	}
	
	
	public void csvParser(String location) throws IOException{
		
		String s="";
		FileReader fr = new FileReader(location);
        BufferedReader bf = new BufferedReader(fr);
        while ( (s= bf.readLine()) != null) {
        	GenotypedData obj =new GenotypedData();
        	int index = 0;
            int len = s.length();
            
            for(; (index  < len) && (s.charAt(index) != '\t'); index++) {}
            obj.rsid= s.substring(0, index);
            
            index++;
            
            char c;
            c = s.charAt(index);
            int chromosome = c & 0xF;
            index++;
            c = s.charAt(index);
            if(c != '\t') {
            	chromosome = (chromosome << 3) + (chromosome << 1) + (c & 0xF);
                index++;
            }
            
          
            
            if((chromosome > CHROMOSOME_COUNT) || (chromosome <=0)) {
            	bf.close();
            	return;
            }
            
            ArrayList <GenotypedData> gen =  genes[chromosome]; 
            index++;
            
            int loc = 0;
            for(;index  < len; index++) {
                c = s.charAt(index);
                if(c != '\t') {
                	loc = (loc << 3) + (loc << 1) + (c & 0xF);
                } else {
                    break;
                }
            }
            obj.location=loc;

            //index++;
            obj.gene1=s.charAt(len-2);
            obj.gene2=s.charAt(len-1);
        	gen.add(obj);
        	
        	
        }
		bf.close();
		IntStream.range(1,CHROMOSOME_COUNT).parallel().forEach(x -> Collections.sort(genes[x]));
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
	
	public void DNAMatchUsingCustomObjects(LinkedHashMap <Integer, SortedSet<FrameData>> current,LinkedHashMap <Integer, SortedSet<FrameData>> party) {
		int count=0, chromosome=0, aliceSize=0,bobSize=0 ;
		
		System.out.println("\n\n\n\t\t\t****FRAME MATCH RESULTS****");
		for(Map.Entry<Integer, SortedSet<FrameData>> entry : current.entrySet()) {
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
		//if (matchingFrames.containsKey(chromosome))	
			 //matchingFrames.get(chromosome).put();
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
		
		//outer map iterator to traverse genotype data of each chromosome
		for(Map.Entry<Integer, SortedMap<Integer, String>> entry : locGene.entrySet()) {		
			chromosome=(entry.getKey());
			SortedSet<FrameData> set = new TreeSet<FrameData>();
			SortedSet<FrameData> l2set = new TreeSet<FrameData>();
			// SortedMap Iterator containing location and genotype
			SortedMap<Integer, String> temp = entry.getValue(); 
			Set<Entry<Integer, String>> sm =temp.entrySet();
			Iterator<Entry<Integer, String>> i=sm.iterator();
			int counter=0,start=0,end=0, l2Start=0,l2End=0,x=1,y=3;
			StringBuilder substring= new StringBuilder("");
			StringBuilder l2Substring =new StringBuilder("");
			while (i.hasNext()) 
	        { 
				
				Map.Entry<Integer, String> m = (Map.Entry<Integer, String>)i.next();
				
				location=(Integer) m.getKey();
				counter++;
				// Beginning of Frame to capture start location and initialize the substing to empty
				if (counter==1 || (counter % t1 ==1)) {		
					start=(Integer) m.getKey();
					substring.delete(0, substring.length());
				}  
				// for each location increasing the counter
	            				   
	            // capture the genotype value
	            String value = (String) m.getValue(); 		
	            // form a substring only if its homozygous and the locations match in both parties
	            if (isHomozygous(value)  && findLocationInMaps(chromosome,location,gene)) // && findLocationInMaps(chromosome,location,gene)					
	            	substring.append(value);
	            		
	            //end of frame, capture end location and rsid to form the Frame Linked hashmap 
	            if (counter % t1 == 0 && start > 0) {			
					end=(Integer) m.getKey();
					//counter=0;                    
					String startRsid=findRsid(chromosome,start);
					String endRsid=findRsid(chromosome,end);

                    // OFK: eventual security issue: we don't want to use frames whose substrings are too short (have to set threshold)
                    // because someone could try to determine the substring from the SHA by brute force
					// Zee: Will work on that(T1 is set for 700)
					//System.out.println("\n Chromosome : "+chromosome+" || Start : "+start+" || RSID : "+startRsid+" || End : "+end+" || RSID : "+endRsid+" || String of Alleles : " +substring+" || Hashed Value : "+getSHA(substring.toString()));
					
					FrameData obj=new FrameData(start,startRsid,end,endRsid,getSHAWitnNonce(substring.toString(),nonce));
					set.add(obj);
					start=0;
					substring.delete(0, substring.length());
				}
	            
	            if (counter == ( x * (t1/n) + 1) ) {
	            	
	            	x+=2;
	            	l2Start=(Integer) m.getKey();
	            	l2Substring.delete(0, l2Substring.length());
	            	//System.out.println(l2Start);
	            }
	            
	            
	            if (counter == (y*(t1/n))) {
	            	y+=2;
	            	l2End=(Integer) m.getKey();
	            	FrameData obj=new FrameData(l2Start,findRsid(chromosome,l2Start),l2End,findRsid(chromosome,l2End),getSHAWitnNonce(l2Substring.toString(),nonce));
	            	l2set.add(obj);
	            	
	            	l2Substring.delete(0, l2Substring.length());
	            	//System.out.println(l2End);
	            }
	            
	            if (counter>t1/n) {
	            	if (isHomozygous((String) m.getValue())  && findLocationInMaps(chromosome,location,gene))
	            		l2Substring.append((String) m.getValue());
	            	
	            }
	            
	            
	            
	            
	            
	        } 
			level1Frame.put(chromosome,set);
			level2Frame.put(chromosome,l2set);
		}
		//System.out.println("Level1 Frame\n\n");
		//displaySet(level1Frame);
		//System.out.println("Level2 Frame\n\n");
		//displaySet(level2Frame);
	}
	 
	//Method to check if the chromsome is between 1-22, rest are ignored
	public boolean isParsable(String s) {
		if  (Character.isDigit(s.charAt(0))) {
			if(Integer.parseInt(s) <= 22)
				return true;
		}
		return false;
			
	}
	
	

	
	
	//Method to check is an allele is Homozygous or not
	public boolean isHomozygous(String allele) {
		return (allele.charAt(0)==allele.charAt(1));
	}
	
	//Method to find RSID given the chromosome and location
	public String findRsid(int key, int location) {
		return locRsid.get(key).get(location);
	}
	
	//Method to find Genotype given the chromosome and location
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
            System.out.println("Exception thrown for incorrect algorithm: " + e); 
  
            return null; 
        } 
    } 
	
	
	
	
	
	public void readFile(String location) throws IOException{
		//int count =0;
		String s="";
		FileReader fr = new FileReader(location);
        BufferedReader bf = new BufferedReader(fr);
        while ( (s= bf.readLine()) != null) {
        	
        	int index = 0;
            int len = s.length();
            
            for(; (index  < len) && (s.charAt(index) != '\t'); index++) {}
            String rsid= s.substring(0, index);
            
            index++;
            
            char c;
            c = s.charAt(index);
            int chromosome = c & 0xF;
            index++;
            c = s.charAt(index);
            if(c != '\t') {
            	chromosome = (chromosome << 3) + (chromosome << 1) + (c & 0xF);
                index++;
            }
            
          
            
            if((chromosome > 22) || (chromosome <=0)) {
            	bf.close();
            	//System.out.println("Number of rows : "+count );
            	return;
            	}
            
            index++;
            
            int loc = 0;
            for(;index  < len; index++) {
                c = s.charAt(index);
                if(c != '\t') {
                	loc = (loc << 3) + (loc << 1) + (c & 0xF);
                } else {
                    break;
                }
            }
           
            String genotype=s.substring(len-2,len);
            
            if ( locGene.containsKey(chromosome) ) {
				locGene.get(chromosome).put(loc,genotype);
				locRsid.get(chromosome).put(loc,rsid);
			}
            else{
				SortedMap <Integer,String> sm=new TreeMap<Integer,String>();
				SortedMap <Integer,String> sm1=new TreeMap<Integer,String>();
				sm.put(loc,genotype);
				sm1.put(loc,rsid);
				locGene.put(chromosome,sm);	
				locRsid.put(chromosome,sm1);
			}
            //count++;
            
            
            
        }
        
        
        bf.close();
        //System.out.println("Number of rows : "+count );
        }
	
	
	
	
	public void read(String location) throws IOException{
		ArrayList<String> al=new ArrayList<String>();
		String line="";
		FileReader fr = new FileReader(location);
		BufferedReader bf =new BufferedReader(fr);
		while ((line = bf.readLine()) != null) {
			al.add(line);
		}
		bf.close();
		System.out.println(al);
	}
	
	
	
	
	
}



























