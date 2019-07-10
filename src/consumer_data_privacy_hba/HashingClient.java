package consumer_data_privacy_hba;

public class HashingClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ConsumerDataPrivacyHBA bob = new ConsumerDataPrivacyHBA();
		ConsumerDataPrivacyHBA alice = new ConsumerDataPrivacyHBA();

		//obj.readFromAlice();
		//obj.readFromBob();
		//obj.hashMatch();
		bob.readFile("input/bob.txt");
		System.out.println("\nContents of LocGene : \n" +bob.locGene);
		System.out.println("\nContents of LocRsid : \n" +bob.locRsid);
		bob.implementFrames();
		
		alice.readFile("input/alice.txt");
		System.out.println("\nContents of LocGene : \n" +alice.locGene);
		System.out.println("\nContents of LocRsid : \n" +alice.locRsid);
		alice.implementFrames();

		

	}

}