package consumer_data_privacy_hba;

public class HashingClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ConsumerDataPrivacyHBA obj = new ConsumerDataPrivacyHBA();
		//obj.readFromAlice();
		//obj.readFromBob();
		//obj.hashMatch();
		obj.readFile("input/bob.txt");
		System.out.println("\nContents of LocGene : \n" +obj.locGene);
		System.out.println("\nContents of LocRsid : \n" +obj.locRsid);
		//obj.readFile("input/bob.txt");
		//obj.putInClass("input/alice.txt");
		obj.implementFrames();

		

	}

}
