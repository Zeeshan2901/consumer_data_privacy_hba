package consumer_data_privacy_hba;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class exclsuionList {
	
	public static void main(String a[]) {
		
		String s="";
		String row[];
		String filename="input/rejects.csv";
		int e_50_60=0, e_less_50=0, e_60_70=0, e_70_80=0, e_80_90=0, e_more_90=0;
		int o_50_60=0, o_less_50=0, o_60_70=0, o_70_80=0, o_80_90=0, o_more_90=0;
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader bf = new BufferedReader(fr);
			while ( ( s= bf.readLine()) != null) {
				row=s.split(",");
				
			}
			bf.close();
			fr.close();
		} catch (IOException e) {
			System.out.println(e); 
		}
		
		
	}

}
