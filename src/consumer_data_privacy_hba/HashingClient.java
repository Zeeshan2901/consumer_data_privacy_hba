package consumer_data_privacy_hba;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HashingClient {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		long startTime,endTime,duration;
		
		//System.out.println("Creating objects !!");
	    startTime = System.nanoTime();
		ConsumerDataPrivacyHBA<?> bob = new ConsumerDataPrivacyHBA<Object>();
		ConsumerDataPrivacyHBA<?> alice = new ConsumerDataPrivacyHBA<Object>();
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n Object creation :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		//obj.readFromAlice();
		//obj.readFromBob();
		//obj.hashMatch();
		
		// Generating Nonce
		//System.out.println("\n Generating Nonce Started");
		startTime = System.nanoTime();
		bob.hashOfPartyNonce=alice.sendHash();
		alice.hashOfPartyNonce=bob.sendHash();
		
		
		//System.out.println("\n HASH sent by Alice to BOB : " +bob.hashOfPartyNonce);
		//System.out.println("\n HASH sent by Bob to Alice : " +alice.hashOfPartyNonce);
		
		bob.party_nonce=alice.sendRandom();
		alice.party_nonce=bob.sendRandom();
		
		//System.out.println("\n Random Number sent by Alice to BOB : " +bob.party_nonce);
		//System.out.println("\n Random Number sent by Bob to Alice : " +alice.party_nonce);
		
		//System.out.println("\n ALICE's Data:");
		//alice.displayNonce();
		
		//System.out.println("\n BOB's Data:");
		//bob.displayNonce();
		
		if (alice.verifyHashNonce() && bob.verifyHashNonce())
			System.out.print("");
		else {
			if (!alice.verifyHashNonce())
				System.out.println("\n\t\t ***** Bob is DISHONEST ***** ");
			if(!bob.verifyHashNonce())
				System.out.println("\n\t\t ***** ALICE is DISHONEST ***** ");
		}
			
		
		alice.caluclateNonce();
		bob.caluclateNonce();
		
		//System.out.println("\n Generating Nonce Ended !!");
		
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n Nonce creation :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		
		//System.out.println("\n Reading Files started");
		startTime = System.nanoTime();
		bob.readFile("input/dad_all.txt");
		alice.readFile("input/sister_all.txt");
		
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n File Read :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		
		//System.out.println("\n Reading Files Ended");
		
		
		//System.out.println("Special Characters -- () invoked");
		startTime = System.nanoTime();
		bob.removeSpecial(alice.locGene);
		alice.removeSpecial(bob.locGene);
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n -- char removal :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		//System.out.println("Special Characters -- () ended");
		//bob.locationMatch(alice.locGene);
		//alice.locationMatch(bob.locGene);
		
		//System.out.println("\n\n\t\t\t\t ***** BOB's Data ***** ");
		//System.out.println("\nContents of LocGene : \n" +bob.locGene);
		//System.out.println("\nContents of LocRsid : \n" +bob.locRsid);
		
		
		//System.out.println("\n Frames Implementation started");
		startTime = System.nanoTime();

		bob.implementFrames(alice.locGene);
		
		//System.out.println("\n\n\t\t\t\t ***** ALICE's Data ***** ");
		//System.out.println("\nContents of LocGene : \n" +alice.locGene);
		//System.out.println("\nContents of LocRsid : \n" +alice.locRsid);
		alice.implementFrames(bob.locGene);
		
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n Frames :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		//System.out.println("\n Frames Implementation Ended!!");
		
		//System.out.println("\n\n\t\t\t\t ***** DNA Match Results ***** ");
		//bob.hashMatch(alice.level1Frames);
		
		//bob.locMatch(alice.level1Frames);
		
		
		//System.out.println("\n Frame Matching");
		startTime = System.nanoTime();

		//bob.DNAMatchUsingCustomObjects(bob.level1Frame,alice.level1Frame);
		//bob.DNAMatchUsingCustomObjects(bob.level2Frame,alice.level2Frame);
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n Matching :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		//alice.showSpecial();
		//bob.showSpecial();
		
		//bob.displaySet(bob.matchingFrames);
		
		//bob.displaySet(bob.matchingFrames);
		
		ConsumerDataPrivacyHBA<?> a = new ConsumerDataPrivacyHBA<Object>();
		ConsumerDataPrivacyHBA<?> b = new ConsumerDataPrivacyHBA<Object>();
		
		startTime = System.nanoTime();
		b.csvParser("input/dad_all.txt");
		a.csvParser("input/sister_all.txt");
		
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n File Read Simple :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		
		for (int i =0 ; i<a.genes.length; i++)
			System.out.println("Chromosome: "+i+" Data: "+a.genes[i]);
			

	}

}