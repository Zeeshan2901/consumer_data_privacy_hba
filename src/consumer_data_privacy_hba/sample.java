package consumer_data_privacy_hba;

public class sample {

	public static void main(String[] args) {
		
		for (int i=1; i<15; i++) {
			int z =(int)(Math.pow(2, i))/1000;
			
			System.out.println("I = "+i+" Z : "+(z+1)+" Circuit size : "+(Math.pow(2, i))+ " Expression : "+ solve(z+1, i));
		}

	}
	
	
	static String solve( int thresh, int nbits) {
		String solution="";
		int i;
		
		if (thresh <= 0 || nbits < 1) return "TRUE";
		// bit positions bigger than threshold's biggest
		for (i = nbits-1; (i >= 0) && ((thresh & (0x1 << i)) == 0); --i)
		    solution +="" + i+" OR ";
		if (i >= 0) {
		    String s = solve(thresh ^ (0x1 << i), i);
		    if (s.equals("TRUE"))
			solution += "" + i;
		    else
			solution += (" ( "+i+" AND ( "+s+" ) ) ");
		}
		return solution;
	}

}
