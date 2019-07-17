package consumer_data_privacy_hba;

public class FrameData {
	
	int start,end;
	String startRSID,endRSID, hashValue;
	
	public FrameData(int st, String stRsid, int en, String enRsid, String hash) {
		start=st;
		startRSID=stRsid;
		end=en;
		endRSID=enRsid;
		hashValue=hash;
	}

}
