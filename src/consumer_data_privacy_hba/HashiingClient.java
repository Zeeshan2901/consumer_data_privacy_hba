package consumer_data_privacy_hba;

public class HashiingClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ConsumerDataPrivacyHBA obj = new ConsumerDataPrivacyHBA();
		obj.readFromAlice();
		obj.readFromBob();
		obj.hashMatch();
		obj.readFile("input/alice.txt");
		//obj.readFile("input/bob.txt");
		

	}

}
