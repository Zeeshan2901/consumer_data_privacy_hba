package consumer_data_privacy_hba;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;



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
		
		a.displayNonce();
		
		System.out.println("\n1");
		b.csvParser("input/sister_all.txt");
		a.csvParser("input/dad_all.txt");
		
		System.out.println("\n2");
		
	
		
		
		
		//Match Locations Code for both the parties
		/////////////////////////////////////
		
		for (int x=1; x<=22;x++) {
			for(int i=0, j=0; i < a.genes[x].size() && j < b.genes[x].size(); i++, j++) {
				GenotypedData cur = (GenotypedData) a.genes[x].get(i);
				GenotypedData par = (GenotypedData) b.genes[x].get(j);
				if (cur.location > par.location) {
					for (int k=j; k < b.genes[x].size(); k++) {
						GenotypedData obj = (GenotypedData) b.genes[x].get(k);
						if(cur.location > obj.location) {
							b.genes[x].remove(k);
							k--;
						}
						else if (cur.location == obj.location && cur.rsid.contentEquals(obj.rsid)) {
							j=k;
							break;
						}
						else if (cur.location < obj.location) {
							j=i;
							j--;
							i--;
							break;
						}
					}
				}
				else if (cur.location < par.location) {
					for (int k=i; k < a.genes[x].size(); k++) {
						GenotypedData obj = (GenotypedData) a.genes[x].get(k);
						if (obj.location < par.location) {
							a.genes[x].remove(k);
							k--;
						}
						else if (obj.location == par.location) {
							i=k;
							break;
						}
						else if (obj.location > par.location) {
							i=j;
							j--;
							i--;
							break;
						}
					}
					
				}
			}
			if (a.genes[x].size() > b.genes[x].size()) 
				a.genes[x].subList(b.genes[x].size(), a.genes[x].size()).clear();
			else if (b.genes[x].size() > a.genes[x].size())
				b.genes[x].subList(a.genes[x].size(), b.genes[x].size()).clear();
			
			
		}
		
		
		////////////////////////////////////
		
		//for (int i=1;i<=22;i++) {
		//	System.out.println("\n Chromosome : "+i+" || Size : "+a.genes[i].size());
		//	System.out.println("\n Chromosome : "+i+" || Size : "+b.genes[i].size());
		//}
		
		
		
		
		System.out.println("\n3");
		
	    b.removeSpcChars(a.genes);
	    a.removeSpcChars(b.genes);
		
	    System.out.println("\n4");
	    
	    b.setFrames(a.genes);
	    
	    System.out.println("\n4.1");

	    
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
		b.displayExclusionList();
		/*
		for (int i=1;i<=22;i++) 
			for(int j=0; j< b.match[i].size(); j++) {
				FrameData obj=(FrameData) b.match[i].get(j);
				obj.display(obj, i);
			}
		*/
		
	   
	}

}