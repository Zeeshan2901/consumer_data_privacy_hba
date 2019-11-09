package consumer_data_privacy_hba;

public class FrameData implements Comparable <FrameData> {
	
	int start,end,cmStart,cmEnd;
	String startRSID,endRSID;
	String evenHashValue, oddHashValue;
	boolean sent;
	boolean match;
	
	public FrameData(int st, String stRsid, int en, String enRsid, String ehash, String ohash, int cms, int cme) {
		start=st;
		startRSID=stRsid;
		end=en;
		endRSID=enRsid;
		evenHashValue=ehash;
		oddHashValue=ohash;
		cmStart=cms;
		cmEnd=cme;
		sent=false;
		match=false;
	}
	
	public FrameData(FrameData obj) {
		this.end=obj.end;
		this.start=obj.start;
		this.startRSID=obj.startRSID;
		this.endRSID=obj.endRSID;
		this.evenHashValue=obj.evenHashValue;
		this.oddHashValue=obj.oddHashValue;
		this.sent=false;
		this.match=false;
	}
	
	
	public void display(FrameData ob, int chromo) {
		System.out.println("\n Chromosome : "+chromo+" || Start :" +ob.start+" || StartRSID : "+ob.startRSID+" "
				+ "|| End : "+ob.end+" || EndRSID : "+ob.endRSID+ " || CM_Start : " +ob.cmStart + " || CM_End : "+ob.cmEnd+
				" || HashValues : "+ob.evenHashValue+" || "
				+ob.oddHashValue + " || Sent : "+ob.sent+" || Match : "+ob.match);
	}
	
	public int compareTo(FrameData fd) {
		if (start==fd.start)
			return 0;
		if (fd.start < start)
			return 1;
		else 
			return -1;
	}

}
