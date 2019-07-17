package consumer_data_privacy_hba;

public class HashingClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ConsumerDataPrivacyHBA bob = new ConsumerDataPrivacyHBA();
		ConsumerDataPrivacyHBA alice = new ConsumerDataPrivacyHBA();

		//obj.readFromAlice();
		//obj.readFromBob();
		//obj.hashMatch();
		
		// Generating Nonce
		bob.hashOfPartyNonce=alice.sendHash();
		alice.hashOfPartyNonce=bob.sendHash();
		
		
		System.out.println("\n HASH sent by Alice to BOB : " +bob.hashOfPartyNonce);
		System.out.println("\n HASH sent by Bob to Alice : " +alice.hashOfPartyNonce);
		
		bob.party_nonce=alice.sendRandom();
		alice.party_nonce=bob.sendRandom();
		
		System.out.println("\n Random Number sent by Alice to BOB : " +bob.party_nonce);
		System.out.println("\n Random Number sent by Bob to Alice : " +alice.party_nonce);
		
		System.out.println("\n ALICE's Data:");
		alice.displayNonce();
		
		System.out.println("\n BOB's Data:");
		bob.displayNonce();
		
		
		if (alice.verifyHashNonce())
			System.out.println("\n\t\t ***** BOB is HONEST ***** ");
		else
			System.out.println("\n\t\t ***** BOB is DISHONEST ***** ");
		
		if (bob.verifyHashNonce())
			System.out.println("\n\t\t ***** ALICE is HONEST ***** ");
		else
			System.out.println("\n\t\t ***** ALICE is DISHONEST ***** ");
		
		alice.caluclateNonce();
		bob.caluclateNonce();
		
		
		bob.readFile("input/sister.txt");
		alice.readFile("input/dad.txt");
		
		
		bob.removeSpecial(alice.locGene);
		alice.removeSpecial(bob.locGene);
		
		
		//bob.locationMatch(alice.locGene);
		//alice.locationMatch(bob.locGene);
		
		System.out.println("\n\n\t\t\t\t ***** BOB's Data ***** ");
		//System.out.println("\nContents of LocGene : \n" +bob.locGene);
		//System.out.println("\nContents of LocRsid : \n" +bob.locRsid);
		bob.implementFrames(alice.locGene);
		
		System.out.println("\n\n\t\t\t\t ***** ALICE's Data ***** ");
		//System.out.println("\nContents of LocGene : \n" +alice.locGene);
		//System.out.println("\nContents of LocRsid : \n" +alice.locRsid);
		alice.implementFrames(bob.locGene);
		
		
		
		
		System.out.println("\n\n\t\t\t\t ***** DNA Match Results ***** ");
		bob.hashMatch(alice.level1Frames);
		
		bob.locMatch(alice.level1Frames);
		
		alice.showSpecial();
		bob.showSpecial();
		

	}

}