package consumer_data_privacy_hba;

import java.io.IOException;

public class HashingClient {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException {
				
		ConsumerDataPrivacyHBA a = new ConsumerDataPrivacyHBA();
		ConsumerDataPrivacyHBA b = new ConsumerDataPrivacyHBA();
		
		
		b.hashOfPartyNonce=a.sendHash();
		a.hashOfPartyNonce=b.sendHash();
		
		b.party_nonce=a.sendRandom();
		a.party_nonce=b.sendRandom();
		
		if (a.verifyHashNonce() && b.verifyHashNonce())
			System.out.print("");
		else {
			if (!a.verifyHashNonce())
				System.out.println("\n\t\t ***** Bob is DISHONEST ***** ");
			if(!b.verifyHashNonce())
				System.out.println("\n\t\t ***** ALICE is DISHONEST ***** ");
		}
			
		a.caluclateNonce();
		b.caluclateNonce();
		
		b.csvParser("input/dad_all.txt");
		a.csvParser("input/sister_all.txt");
		
	    b.removeSpcChars(a.genes);
	    a.removeSpcChars(b.genes);
		
	    b.setFrames(a.genes);
	    a.setFrames(b.genes);
	    /*
		for (int i=1;i<=22;i++) 
			for(int j=0; j< b.frames[i].size(); j++) {
				FrameData obj=(FrameData) b.frames[i].get(j);
				obj.display(obj, i);
			}
		*/
		b.frameMatch(b.frames, a.frames);
		
		/*
		for (int i=1;i<=22;i++) 
			for(int j=0; j< b.match[i].size(); j++) {
				FrameData obj=(FrameData) b.match[i].get(j);
				obj.display(obj, i);
			}
		
		*/
	   
	}

}