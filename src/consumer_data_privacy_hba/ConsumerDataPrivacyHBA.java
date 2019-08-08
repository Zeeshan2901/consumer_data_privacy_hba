package consumer_data_privacy_hba;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger; 
import java.security.MessageDigest; 
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;


/**
 * @author Zeeshan
 *
 */


public class ConsumerDataPrivacyHBA<genes> {
	
	//Size of Frame
	int t1, n;		
	//Nonce fields
	long my_nonce, party_nonce, nonce;
	String hashOfMyNonce, hashOfPartyNonce;
	
	final static int CHROMOSOME_COUNT =22;
	
	ArrayList<GenotypedData>[] genes;
	ArrayList<FrameData>[] frames;
	ArrayList<FrameData>[] match;
	
	
	
	public ConsumerDataPrivacyHBA() {		
		t1=700;
		n=1;

		genes  = new ArrayList[CHROMOSOME_COUNT+1]; 
		frames = new ArrayList[CHROMOSOME_COUNT+1];
		match  = new ArrayList[CHROMOSOME_COUNT+1];
		for (int i=1; i<=CHROMOSOME_COUNT; i++) {
			genes[i]= new ArrayList<GenotypedData>();
			frames[i]= new ArrayList<FrameData>();
			match[i]= new ArrayList<FrameData>();
		}
	}
	
	
	public void frameMatch(ArrayList<FrameData>[] current,
			ArrayList<FrameData>[]party) {
			int count=0, chromosome=0, aliceSize=0,bobSize=0 ;
			for (int i =1 ; i<=CHROMOSOME_COUNT; i++) {
				count=aliceSize=bobSize=0;
				chromosome=i;
				for(int j=0; j< current[i].size(); j++) {
					FrameData cur=current[i].get(j);
					aliceSize=current[i].size();
					for(int k=0; k<party[i].size();k++) {
						FrameData par=party[i].get(k);
						bobSize=party[i].size();
						if (someMatch(cur,par)) {
							FrameData matchingObject= new FrameData(cur);
							ArrayList <FrameData> gen =  match[i];
							gen.add(matchingObject);
							count++;
							break;
						}
					}
				}
				System.out.println("\n At Chromosome "+chromosome);
				System.out.println("\t\t No. of  Alice's Frames are "+aliceSize);
				System.out.println("\t\t No. of   Bob's  Frames are "+bobSize);
				System.out.println("\t\t No. of matching Frames are "+count);
			}
	}

			
	
	
	//CSVParser to parse the csv file and put the data into
	//ArrayList of defined objects
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
	
	
	//Method to remove special character -- from Arraylist genes
	public void removeSpcChars(ArrayList<GenotypedData>[] locGene) {
		int loc=0;
		int loc1=0;
		for (int i =1 ; i<=CHROMOSOME_COUNT; i++) {
			for(int j=0; j< genes[i].size(); j++) {
				GenotypedData obj= genes[i].get(j);
				if (obj.gene1=='-'&& obj.gene2=='-') {
					loc=obj.getLocation();
					loc1=locGene[i].get(j).getLocation();
					if (loc==loc1) {
						locGene[i].remove(j);
						genes[i].remove(j);
					}	
					else{
						Iterator<GenotypedData> itr=locGene[i].iterator();
						 while (itr.hasNext()) {
							 GenotypedData o=(GenotypedData)itr.next();
							 loc1=o.getLocation();
							 if (loc==loc1) {
								 genes[i].remove(j);
								 itr.remove(); 
								 break;
							 }
						 }
					}
				}
			}
		}
	}
	
	
	
	//Method to implement Frames
	public void frame(int offset,ArrayList<GenotypedData>[] locGene) {
		//iterating the outer array for each 22 chromosomes
		for (int i =1 ; i<=CHROMOSOME_COUNT; i++) {
			StringBuilder evenSubstring= new StringBuilder("");
			StringBuilder oddSubstring= new StringBuilder("");
			int counter =0;
			int start=0;
			int end =0;
			String startRsid="";
			String endRsid="";
		//iterating through the data of each chromosomes
			for(int j=offset; j<genes[i].size(); j++) {
				counter++;
				GenotypedData obj= genes[i].get(j);	
		//capture the start of the frame and empty the genotype string
				if (counter==1 || (counter % t1 ==1)) {
					start=obj.getLocation();
					startRsid=obj.getRSID();
					oddSubstring.delete(0, oddSubstring.length());
					evenSubstring.delete(0, evenSubstring.length());
				}	
		//form a string only if the genotype is homozygous and the location exists in other party and it is homozygus as well 
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
		//capture the end of string 
		//empty the genotype string
		//add the frame data object in sortedset
				if (counter % t1 == 0 && start > 0) {
					ArrayList <FrameData> gen =  frames[i];
					end=obj.location;
					endRsid=obj.getRSID();
					FrameData fr=new FrameData(start,startRsid,end,endRsid,getSHAWitnNonce(evenSubstring.toString(),nonce),getSHAWitnNonce(oddSubstring.toString(),nonce));
					gen.add(fr);
					start=0;
					oddSubstring.delete(0, oddSubstring.length());
					evenSubstring.delete(0, evenSubstring.length());
				}				
			}		
		}		
	}
	
	public void setFrames(ArrayList<GenotypedData>[] locGene) {
		for (int i =0; i<t1; i+=t1/n) {	
	    	frame(i,locGene);
	    }
		IntStream.range(1,CHROMOSOME_COUNT).parallel().forEach(x -> Collections.sort(frames[x]));
	}
	
	
	public boolean ifLocationExistsinOtherParty(int chromosome, int index,GenotypedData obj, ArrayList<GenotypedData>[] locGene) {
		GenotypedData a= locGene[chromosome].get(index);
		if (a.location==obj.location && a.rsid.contentEquals(obj.getRSID())) {
			if (a.gene1==a.gene2)
				return true;
			else
				return false;
		}
		//This part is not executing yet
		int l=0;
		int r= locGene[chromosome].size();
		
		while (l<r) {
			if (l==0 && r== locGene[chromosome].size())
				System.out.println("It came here");
			int m= (l+r)/2;
			GenotypedData o= locGene[chromosome].get(m);
			if (o.location==obj.location && o.rsid.contentEquals(obj.getRSID())) {
				if (o.gene1==o.gene2)
					return true;
				else
					return false;
			}
			else if (o.location<obj.location)
				l=m+1;
			else if (o.location > obj.location)
				r=m-1;
		}		
		return false;
	}
	
	
	public boolean someMatch(FrameData my, FrameData party) {
		return (my.start==party.start && my.end==party.end && 
				(my.oddHashValue.contentEquals(party.oddHashValue) 
						|| my.evenHashValue.contentEquals(party.evenHashValue) 
						) ) ? true : false;		
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
}



























