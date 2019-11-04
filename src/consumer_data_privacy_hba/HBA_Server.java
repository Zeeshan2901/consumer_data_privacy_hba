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

public class HBA_Server {

	// initialize socket and input stream
	private Socket socket = null;
	private ServerSocket server = null;
	private DataInputStream serverIn = null;
	private DataOutputStream serverOut = null;

	int port;

	// Size of Frame
	int t1, n;
	// Nonce fields
	long my_nonce, party_nonce, nonce;
	String hashOfMyNonce, hashOfPartyNonce;

	final static int CHROMOSOME_COUNT = 22;

	ArrayList<GenotypedData>[] genes;

	public HBA_Server() {
		port = 5000;

		genes = new ArrayList[CHROMOSOME_COUNT + 1];

		for (int i = 1; i <= CHROMOSOME_COUNT; i++) {
			genes[i] = new ArrayList<GenotypedData>();
		}
	}

	public static void main(String[] args) throws IOException {

		HBA_Server server = new HBA_Server();
		server.run();
	}

	public void nonceComms(DataInputStream in, DataOutputStream out) {

	}

	public void run() {

		// try block to create connection
		try {
			server = new ServerSocket(port);
			System.out.println("Server started");

			System.out.println("Waiting for a client ...");

			socket = server.accept();
			System.out.println("Connection established with Client ");

			System.out.println("Just connected to " + socket.getRemoteSocketAddress());

		} catch (IOException i) {
			System.out.println(i);
		}

		// block to create input, output objects
		try {

			// takes input from the client socket
			serverIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

			// sends output to the socket
			serverOut = new DataOutputStream(socket.getOutputStream());
		} catch (IOException i) {
			System.out.println(i);
		}

		// block to send and receive data
		try {
			for (int i = 10; i < 100; i += 10) {
				serverOut.writeUTF(String.valueOf(i));
				System.out.println("Client : " + serverIn.readUTF());
			}
		} catch (IOException i) {
			System.out.println(i);
		}

		// Block of code to create nonce
		try {
			serverOut.writeUTF(sendHash());
			hashOfPartyNonce = serverIn.readUTF();

			serverOut.writeLong(sendRandom());
			party_nonce = serverIn.readLong();

			if (verifyHashNonce())
				System.out.print("Other Party Is Honest");
			else {
				System.out.print("Other Party Is Dishonest !!!\nHence Closing Connection");
				try {
					socket.close();
					serverIn.close();
					serverOut.close();
				} catch (IOException i) {
					System.out.println(i);
				}
			}

			caluclateNonce();
			displayNonce();

		} catch (NoSuchProviderException i) {
			System.out.println(i);
		} catch (IOException i) {
			System.out.println(i);
		} catch (NoSuchAlgorithmException i) {
			System.out.println(i);
		}

		// Block to read the input file
		try {
			csvParser("input/son_all.txt");
			serverOut.writeUTF("a");
			removeDupLocs();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 1; i <= 22; i++)
			System.out.println("Size of Chromosome  " + i + " is " + genes[i].size());

		// Block to find common snips
		// Server sends all the snips to Client
		try {
			StringBuilder locs = new StringBuilder("");
			int counter = 0;
			String match = "";
			for (int i = 1; i <= CHROMOSOME_COUNT; i++) {
				System.out.println("Sending data for chromosome : " + i);
				locs.delete(0, locs.length());
				for (int j = 0; j < genes[i].size(); j++) {
					GenotypedData obj = genes[i].get(j);
					int location = obj.getLocation();
					locs.append(location);
					locs.append(" ");
					counter++;
					if (counter == 5000) {
						serverOut.writeUTF("continue");
						serverOut.write(i);
						serverOut.writeUTF(locs.toString());
						locs.delete(0, locs.length());
						counter = 0;
						serverIn.readUTF();
					}
				}
				if (counter > 0 && counter < 5000) {
					serverOut.writeUTF("continue");
					serverOut.write(i);
					serverOut.writeUTF(locs.toString());
					locs.delete(0, locs.length());
					counter = 0;
					serverIn.readUTF();
					match = serverIn.readUTF();
					match = removeLocations(i, match);
				}
			}
			String s = "over";
			serverOut.writeUTF(s);
			serverOut.write(0);
			serverOut.writeUTF("All Data Sent");
			System.out.println("String " + s + " sent");
			serverIn.readUTF();
		} catch (IOException i) {
			System.out.println(i);
		}

		System.out.println("*******All Data Sent");

		/*
		 * // Client receives all the snips from Server
		 * 
		 * try { String line = "", locs1 = ""; int chromosome = 1, nextChromo = 2; int
		 * lastIndex = 0; do { line = serverIn.readUTF(); chromosome = serverIn.read();
		 * locs1 = serverIn.readUTF(); if (chromosome == nextChromo) { nextChromo++;
		 * lastIndex = 0; } // System.out.println(line+" "+chromosome+" "+locs1+" "); if
		 * (!line.contentEquals("over")) lastIndex = removeLocations(chromosome, locs1,
		 * lastIndex); serverOut.writeUTF("done"); } while (!line.equals("over")); }
		 * catch (IOException e) { e.printStackTrace(); }
		 */
		for (int i = 1; i <= 22; i++)
			System.out.println("Size of Chromosome  " + i + " is " + genes[i].size());

		for (int x = 1; x <= 22; x++)
			for (int i = 0; i < genes[x].size() - 2; i++) {
				GenotypedData obj = genes[x].get(i);
				GenotypedData ob = genes[x].get(i + 1);
				if (obj.getLocation() == ob.getLocation()) {
					obj.display(obj);
					ob.display(ob);
				}
			}

		// block to close connections
		try {
			System.out.println("Closing connection");
			socket.close();
			serverIn.close();
			serverOut.close();
		} catch (IOException i) {
			System.out.println(i);
		}
	}

	public void removeDupLocs() {
		for (int i = 1; i <= CHROMOSOME_COUNT; i++)
			for (int j = 0; j < genes[i].size() - 1; j++) {
				GenotypedData obj = genes[i].get(j);
				GenotypedData ob = genes[i].get(j + 1);
				if (obj.getLocation() == ob.getLocation())
					genes[i].remove(j + 1);
			}
	}

	public boolean ifLocationExists(int chromosome, int location) {

		// Binary Search
		int l = 0;
		int r = genes[chromosome].size();

		while (l < r) {
			int m = (l + r) / 2;
			GenotypedData o = genes[chromosome].get(m);
			if (o.location == location)
				return true;
			else if (o.location > location)
				l = m + 1;
			else if (o.location < location)
				r = m - 1;
		}

		return false;
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

	/*
	 * public int removeLocations(int chromosome, String locs, int lastIndex) {
	 * 
	 * int i,j,k; int index=0; String[] temp = locs.split(" "); int[] locations =
	 * new int[temp.length]; for( i = 0; i < temp.length; i++) locations[i] =
	 * Integer.parseInt(temp[i]);
	 * 
	 * 
	 * int loc=0;
	 * 
	 * for ( i=0 , j=lastIndex ; i<locations.length && j< genes[chromosome].size() ;
	 * i++,j++) { GenotypedData obj = genes[chromosome].get(j);
	 * loc=obj.getLocation(); if (loc == locations[i]) continue; else if (loc >
	 * locations[i] ) { int arrayLoc=locations[i]; k=i; while( arrayLoc <= loc && k
	 * < locations.length) { arrayLoc=locations[k++]; } i=k;
	 * 
	 * for( k=i ; locations[k] <= loc && k < locations.length ; k++ ) { continue; }
	 * i=k;
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
	 * } return j--; }
	 */

	// Verify if the alleles are Homozygous and in "A,C,G,T" for
	public boolean isPermissible(char a, char b) {

		if (a != b)
			return false;
		else {
			char[] permissible = { 'A', 'C', 'G', 'T' };
			int x = 0, y = 0;
			for (int i = 0; i < permissible.length; i++) {
				if (a == permissible[i])
					x = 1;
				if (b == permissible[i])
					y = 1;
			}
			if ((x + y) == 2)
				return true;
			else
				return false;
		}

	}

	public void csvParser(String location) throws IOException {

		String s = "";
		FileReader fr = new FileReader(location);
		BufferedReader bf = new BufferedReader(fr);
		while ((s = bf.readLine()) != null) {
			GenotypedData obj = new GenotypedData();
			int index = 0;
			int len = s.length();

			for (; (index < len) && (s.charAt(index) != '\t'); index++) {
			}
			obj.rsid = s.substring(0, index);

			index++;

			char c;
			c = s.charAt(index);
			int chromosome = c & 0xF;
			index++;
			c = s.charAt(index);
			if (c != '\t') {
				chromosome = (chromosome << 3) + (chromosome << 1) + (c & 0xF);
				index++;
			}

			if ((chromosome > CHROMOSOME_COUNT) || (chromosome <= 0)) {
				bf.close();
				return;
			}

			ArrayList<GenotypedData> gen = genes[chromosome];
			index++;

			int loc = 0;
			for (; index < len; index++) {
				c = s.charAt(index);
				if (c != '\t') {
					loc = (loc << 3) + (loc << 1) + (c & 0xF);
				} else {
					break;
				}
			}
			obj.location = loc;

			// index++;
			obj.gene1 = s.charAt(len - 2);
			obj.gene2 = s.charAt(len - 1);

			if (isPermissible(obj.gene1, obj.gene2) && obj.rsid.substring(0, 1).equals("r"))
				gen.add(obj);

		}
		bf.close();
		IntStream.range(1, CHROMOSOME_COUNT).parallel().forEach(x -> Collections.sort(genes[x]));
	}

	// Method to generate a Random number
	public long generateRandom() throws NoSuchAlgorithmException, NoSuchProviderException {

		SecureRandom secureRandomNumber = SecureRandom.getInstance("SHA1PRNG", "SUN");
		byte[] randomBytes = new byte[128];
		secureRandomNumber.nextBytes(randomBytes);
		return my_nonce = secureRandomNumber.nextLong();

		// my_nonce
		// =ThreadLocalRandom.current().nextLong(-9223372036854775807L,9223372036854775807L)
	}

	// Method to send Hash of random number to the other party
	public String sendHash() throws NoSuchAlgorithmException, NoSuchProviderException {
		return (hashOfMyNonce = getSHA(String.valueOf(generateRandom())));
	}

	// Method to send Random number to the other party
	public long sendRandom() {
		return my_nonce;
	}

	// Method to verify if Hash of Random matches with the random number sent by
	// other party
	public boolean verifyHashNonce() {
		return hashOfPartyNonce.contentEquals(getSHA(String.valueOf(party_nonce)));
	}

	// Display the NONCE fields
	public void displayNonce() {
		System.out.println("\n My Data		: " + my_nonce + " :: " + hashOfMyNonce);
		System.out.println("\n Party Data		: " + party_nonce + " :: " + hashOfPartyNonce);
		System.out.println("\n Nonce			: " + nonce);
	}

	// Method to Calculate the Ultimate NONCE
	public void caluclateNonce() {
		nonce = my_nonce ^ party_nonce;
		// System.out.println("\n Nonce : " +nonce);
	}

	// Method to generate Hash with nonce
	public String getSHAWitnNonce(String input, long nonce1) {
		try {
			// Static getInstance method is called with hashing SHA
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			// update the digest with nonce
			md.update(String.valueOf(nonce1).getBytes());
			// digest() method called
			// to calculate message digest of an input
			// and return array of byte
			byte[] messageDigest = md.digest(input.getBytes());
			// Convert byte array into sign-magnitude representation
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
			System.out.println("Exception thrown" + " for incorrect algorithm: " + e);
			return null;
		}
	}

	// Method to generate Hash w/o nonce
	public String getSHA(String input) {
		try {
			// Static getInstance method is called with hashing SHA
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			// digest() method called
			// to calculate message digest of an input
			// and return array of byte
			byte[] messageDigest = md.digest(input.getBytes());
			// Convert byte array into sign-magnitude representation
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
