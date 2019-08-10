package consumer_data_privacy_hba;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;

public class HashingClient {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
				
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
		
		//a.displayNonce();
		
		System.out.println("\n1");
		b.csvParser("input/dad_all.txt");
		a.csvParser("input/son_all.txt");
		
		System.out.println("\n2");
		
		ArrayList<GenotypedData>[] temp=new ArrayList[22+1];
		for (int i=1; i<=22; i++) 
			temp[i]= new ArrayList<GenotypedData>();
			
		
		b.matchLocations(a.genes);
		a.matchLocations(b.genes);
		
		
		
		//a.genes=temp;
		
		
		
		
		
		
		System.out.println("\n3");
		
	    b.removeSpcChars(a.genes);
	    a.removeSpcChars(b.genes);
		
	    System.out.println("\n4");
	    
	    b.setFrames(a.genes);
	    a.setFrames(b.genes);
	    /*
		for (int i=1;i<=22;i++) 
			for(int j=0; j< b.frames[i].size(); j++) {
				FrameData obj=(FrameData) b.frames[i].get(j);
				obj.display(obj, i);
			}
		*/
	    
	    System.out.println("\n5");
	    
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