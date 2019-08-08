package consumer_data_privacy_hba;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HashingClient {

	public static void main(String[] args) throws IOException {
		
		long startTime,endTime,duration;
		
		ConsumerDataPrivacyHBA a = new ConsumerDataPrivacyHBA();
		ConsumerDataPrivacyHBA b = new ConsumerDataPrivacyHBA();
		
		startTime = System.nanoTime();
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
		
		
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n Nonce :"+ TimeUnit.NANOSECONDS.toMillis(duration));

		
		startTime = System.nanoTime();
		
		b.csvParser("input/sample.txt");
		a.csvParser("input/sample.txt");
		
		endTime = System.nanoTime();
		duration=endTime-startTime;
		System .out.println ("\n File Read Simple :"+ TimeUnit.NANOSECONDS.toMillis(duration));
	    
		
		startTime = System.nanoTime();
	    
	    b.removeSpcChars(a.genes);
	    a.removeSpcChars(b.genes);
		
	    endTime = System.nanoTime();
		duration=endTime-startTime;
		System .out.println ("\n Spce Chars Removal :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		
	    startTime = System.nanoTime();
	    
	    b.setFrames(a.genes);
	    a.setFrames(b.genes);
	    
		endTime = System.nanoTime();
		duration=endTime-startTime;
		
		System .out.println ("\n Frame :"+ TimeUnit.NANOSECONDS.toMillis(duration));
		
		
		for (int i=1;i<=22;i++) 
			for(int j=0; j< b.frames[i].size(); j++) {
				FrameData obj=(FrameData) b.frames[i].get(j);
				obj.display(obj, i);
			}
		
		
	   
	}

}