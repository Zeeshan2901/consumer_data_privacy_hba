package consumer_data_privacy_hba;

public class FrameData implements Comparable <FrameData> {
	
	int start,end;
	String startRSID,endRSID, hashValue;
	
	public FrameData(int st, String stRsid, int en, String enRsid, String hash) {
		start=st;
		startRSID=stRsid;
		end=en;
		endRSID=enRsid;
		hashValue=hash;
	}
	
	public void display(FrameData ob, int chromo) {
		System.out.println("\n Chromosome : "+chromo+" || Start :" +ob.start+" || StartRSID : "+ob.startRSID+" || End : "+ob.end+" || EndRSID : "+ob.endRSID+" || HashValue : "+ob.hashValue);
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
