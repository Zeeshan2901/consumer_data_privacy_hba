package consumer_data_privacy_hba;

public class FrameData implements Comparable <FrameData> {
	
	int start,end;
	String startRSID,endRSID;
	String evenHashValue, oddHashValue;
	
	public FrameData(int st, String stRsid, int en, String enRsid, String ehash, String ohash) {
		start=st;
		startRSID=stRsid;
		end=en;
		endRSID=enRsid;
		evenHashValue=ehash;
		oddHashValue=ohash;
	}
	
	public FrameData(FrameData obj) {
		this.end=obj.end;
		this.start=obj.start;
		this.startRSID=obj.startRSID;
		this.endRSID=obj.endRSID;
		this.evenHashValue=obj.evenHashValue;
		this.oddHashValue=obj.oddHashValue;
	}
	
	
	public void display(FrameData ob, int chromo) {
		System.out.println("\n Chromosome : "+chromo+" || Start :" +ob.start+" || StartRSID : "+ob.startRSID+" "
				+ "|| End : "+ob.end+" || EndRSID : "+ob.endRSID+" || HashValues : "+ob.evenHashValue+" || "+ob.oddHashValue);
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
