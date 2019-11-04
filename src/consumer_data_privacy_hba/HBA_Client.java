package consumer_data_privacy_hba;

import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.IntStream;
import java.io.*;
import java.math.BigInteger; 

public class HBA_Client {
	
	// initialize socket and input, output streams 
    private Socket 			 socket            = null; 
    private DataInputStream  clientIn   = null; 
    private DataOutputStream clientOut     = null; 
    
    String clientAddress;
    int port;
    
    
    //Size of Frame
  	int t1, n;
  	//Nonce fields
  	long my_nonce, party_nonce, nonce;
  	String hashOfMyNonce, hashOfPartyNonce;
  	
  	
    
    final static int CHROMOSOME_COUNT =22;
    
    
    ArrayList<GenotypedData>[] genes;
    
    public HBA_Client() {
    	clientAddress="127.0.0.1"; 
    	port=5000;
    	
    	genes  = new ArrayList[CHROMOSOME_COUNT+1]; 
    	
    	for (int i=1; i<=CHROMOSOME_COUNT; i++) {
    		genes[i]= new ArrayList<GenotypedData>();
    	}
    }

	

	public static void main(String[] args) throws IOException {
		
		HBA_Client client = new HBA_Client();
		//client.csvParser("input/dad_all.txt");
		
		client.run();
	}
	
	// establishing a connection
	public void run() throws IOException {
		
		//Block to establish the connection 
        try
        { 
            socket = new Socket(clientAddress, port); 
            System.out.println("Connected"); 
            
            System.out.println("Connection established with Server ");
            
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());
     
        } 
        catch(UnknownHostException u) 
        { 
            System.out.println(u); 
        } 
        catch(IOException i) 
        { 
            System.out.println(i); 
        }
		
      //block to create input, output objects
  		try {
			// takes input from the client socket 
			clientIn = new DataInputStream( 
			    new BufferedInputStream(socket.getInputStream()));
			
			// sends output to the socket 
			clientOut= new DataOutputStream(socket.getOutputStream());
  		}catch(IOException i) 
          { 
              System.out.println(i); 
          }
        
        //block to send and receive data 
        try {
        		for (int i=100; i< 1000; i+=100) {
        			clientOut.writeUTF(String.valueOf(i));
        			System.out.println("Server : " + clientIn.readUTF());
        		}
        }catch(IOException i) 
        { 
            System.out.println(i); 
        } 
        
        //Block of code to create and verify nonce
        try {
        	
        	hashOfPartyNonce=clientIn.readUTF();
        	clientOut.writeUTF(sendHash());

        	party_nonce=clientIn.readLong();
        	clientOut.writeLong(sendRandom());
        	
        	
        	if (verifyHashNonce())
    			System.out.print("Other Party Is Honest");
    		else {
    			System.out.print("Other Party Is Dishonest !!!\nHence Closing Connection");
    			try {
    				clientIn.close(); 
    	        	clientOut.close(); 
    	            socket.close(); 
    	        }catch(IOException i) 
    	        { 
    	            System.out.println(i); 
    	        }
    		}
        	
        	
        	caluclateNonce();
        	displayNonce();	
        }catch(NoSuchProviderException i) 
        { 
            System.out.println(i); 
        } catch(IOException i) 
        { 
            System.out.println(i); 
        }catch(NoSuchAlgorithmException i) 
        { 
            System.out.println(i); 
        } 
        
        
        
      //Block to read the input file
        try {
        	clientIn.readUTF();
			csvParser("input/sister_all.txt");
			removeDupLocs();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
        for (int i =1; i<=22;i++) 
        	System.out.println("Size of Chromosome  "+i + " is "+genes[i].size());
        
        
        //Block to find common snips
        //Client receives all the snips from Server

        try {
        	String line="",locs="",finalChromosomeLocation="";
            int nextChromo=2,chromosome=1;
            String match="";
            while(!line.equals("over")) {
            	line=clientIn.readUTF();
            	chromosome=clientIn.read();
            	locs=clientIn.readUTF();
            	finalChromosomeLocation += locs;
            	clientOut.writeUTF("done");
            	if (chromosome==nextChromo) {
            		nextChromo++;
            		if (!line.contentEquals("over"))
                		match=removeLocations(chromosome,finalChromosomeLocation);
            		clientOut.writeUTF(match);
            	}
            	//System.out.println(line+" "+chromosome+" "+locs+" ");
    			//System.out.println("Processed");
            }
        } catch (IOException e) {
        	e.printStackTrace();
        }
       
        System.out.println("*******All Data Received");
        
		/*
		 * //Client sends all the snips to Server try { StringBuilder locs1 = new
		 * StringBuilder(""); int counter=0,x=0; for (int i =1; i<=CHROMOSOME_COUNT;
		 * i++) { System.out.println("Sending data for chromosome : " +i);
		 * locs1.delete(0, locs1.length()); for(int j=0;j<genes[i].size();j++) {
		 * GenotypedData obj= genes[i].get(j); int location=obj.getLocation();
		 * locs1.append(location); locs1.append(" "); counter++; if (counter ==5000) {
		 * //System.out.println("\t\t\t"+x++); clientOut.writeUTF("continue");
		 * clientOut.write(i); clientOut.writeUTF(locs1.toString()); locs1.delete(0,
		 * locs1.length()); counter=0; clientIn.readUTF(); } } } String s="over";
		 * clientOut.writeUTF(s); clientOut.write(0);
		 * clientOut.writeUTF("All Data Sent"); System.out.println("String "+s+" sent");
		 * clientIn.readUTF(); } catch(IOException i) { System.out.println(i); }
		 */
        
        
        
        for (int i =1; i<=22;i++) 
        	System.out.println("Size of Chromosome  "+i + " is "+genes[i].size());
        
        
		/*
		 * for (int i=0;i<genes[22].size();i++) { GenotypedData obj= genes[22].get(i);
		 * obj.display(obj); }
		 */
        
        
        
        
        
        
        
        
        
        
        // close the connection 
        try
        { 
        	clientIn.close(); 
        	clientOut.close(); 
            socket.close(); 
        } 
        catch(IOException i) 
        { 
            System.out.println(i); 
        } 
	}
	
	public void removeDupLocs() {
		for (int i=1 ; i<= CHROMOSOME_COUNT; i++)
			for (int j=0; j<genes[i].size()-1; j++ ) {
				GenotypedData obj= genes[i].get(j);
				GenotypedData ob= genes[i].get(j+1); 
				if (obj.getLocation()==ob.getLocation()) 
					genes[i].remove(j+1);
			}
	}
	
	public String removeLocations(int chromosome, String locs) {
		
		int i,j;
		String[] temp = locs.split(" ");
		int[] locations = new int[temp.length];
		for( i = 0; i < temp.length; i++) 
			locations[i] = Integer.parseInt(temp[i]);
		
		
		int arrayValue=0, objValue=0;
		String matchedLocs="";
		i=j=0;
		while (i<locations.length && j< genes[chromosome].size() ) {
			 arrayValue=locations[i];
			 GenotypedData obj=genes[chromosome].get(j);
			 objValue=obj.getLocation();
			 
			 if (arrayValue==objValue) {
				 matchedLocs += arrayValue + " ";
				 i++;
				 j++;
			 }
			 
			 else if (arrayValue < objValue) {
				 while(arrayValue<objValue) {
					 arrayValue=locations[i++];
				 }
			 }
			 else if (arrayValue > objValue) {
				 while(arrayValue > objValue) {
					 GenotypedData obj1=genes[chromosome].get(j);
					 objValue=obj1.getLocation();
					 if (arrayValue > objValue) {
						 genes[chromosome].remove(j);
						 j--;
					 }
					 else if (arrayValue==objValue) {
						 matchedLocs += arrayValue + " ";
						 i++;
						 j++;
					 }
					 else if (arrayValue<objValue) {
						 while(arrayValue<objValue) {
							 arrayValue=locations[i++];
						 }
					 }
				 }
				 
				 
			 }
			
		}
		
		return matchedLocs;
		
		/*
		 * for ( i=0 , j=0 ; i<locations.length && j< genes[chromosome].size() ;
		 * i++,j++) { GenotypedData obj = genes[chromosome].get(j);
		 * loc=obj.getLocation(); if (loc == locations[i]) continue; else if (loc >
		 * locations[i] ) { int arrayLoc=locations[i]; k=i; while( arrayLoc <= loc && k
		 * < locations.length) { arrayLoc=locations[k++]; } i=k;
		 * 
		 * } else if ( loc < locations[i]) {
		 * 
		 * for(k=j; k< genes[chromosome].size(); k++) { GenotypedData ob =
		 * genes[chromosome].get(k); if (ob.getLocation() < locations[i] ) {
		 * genes[chromosome].remove(k); k--; } if (ob.getLocation() >= locations[i] ) {
		 * break; } } j=k;
		 * 
		 * }
		 * 
		 * }
		 */
	}
	
	
	
	public boolean ifLocationExists(int chromosome, int location) {
		
		//Binary Search
		int l=0;
		int r= genes[chromosome].size();
		
		while (l<r) {
			int m= (l+r)/2;
			GenotypedData o= genes[chromosome].get(m);
			if (o.location==location ) 
				return true;
			else if (o.location > location)
				l=m+1;
			else if (o.location < location)
				r=m-1;
		}		
		
		return false;
	}

	// Verify if the alleles are Homozygous and in "A,C,G,T" for 
	public boolean isPermissible (char a, char b) {
		
		if (a!=b)
			return false;
		else {
			char [] permissible= {'A','C','G','T'};
			int x=0,y=0;
			for (int i=0; i< permissible.length;i++) {
				if (a==permissible[i])
					x=1;
				if (b==permissible[i])
					y=1;
			}
			if ((x+y)==2) return true ;
			else return false;
		}
		
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
            
            if (isPermissible(obj.gene1,obj.gene2) && obj.rsid.substring(0, 1).equals("r"))
            	gen.add(obj);
        	
        	
        }
		bf.close();
		IntStream.range(1,CHROMOSOME_COUNT).parallel().forEach(x -> Collections.sort(genes[x]));
	}


	//Method to generate a Random number
	public long generateRandom() throws NoSuchAlgorithmException, NoSuchProviderException {
		
		SecureRandom secureRandomNumber = SecureRandom.getInstance("SHA1PRNG", "SUN");
		byte[] randomBytes = new byte[128];
		secureRandomNumber.nextBytes(randomBytes);
		return my_nonce=secureRandomNumber.nextLong();
		
		//my_nonce =ThreadLocalRandom.current().nextLong(-9223372036854775807L,9223372036854775807L)
	}
	
	//Method to send Hash of random number to the other party
	public String sendHash() throws NoSuchAlgorithmException, NoSuchProviderException {
		return(hashOfMyNonce=getSHA(String.valueOf(generateRandom())));
	}
	
	
	//Method to send Random number to the other party
	public long sendRandom() {
		return my_nonce;
	}
	//Method to verify if Hash of Random matches with the random number sent by other party
	public boolean verifyHashNonce() {
		return hashOfPartyNonce.contentEquals(getSHA(String.valueOf(party_nonce)));
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
	
	//Display the NONCE fields
	public void displayNonce() {
		System.out.println("\n My Data		: "+my_nonce+" :: "+hashOfMyNonce);
		System.out.println("\n Party Data		: "+party_nonce+" :: "+hashOfPartyNonce);
		System.out.println("\n Nonce			: "+nonce);
	}
	
	//Method to Calculate the Ultimate NONCE
	public void caluclateNonce() {
		nonce=my_nonce^party_nonce;
		//System.out.println("\n Nonce : " +nonce);
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
