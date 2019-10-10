package consumer_data_privacy_hba;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CentiMorgans {

	
	public static void main(String a[]) throws IOException {
		
		String[] files = new String[24];
		String dir="/home/zeeshan/Downloads/rutgers_map_v3a/";
		
		for (int i=1;i<=22;i++) {
			files[i]=dir + "RUMapv3a_B137_chr" + i + ".txt";
			//System.out.println("\n***************************************** Chromosome :   "+i);
			parseAndLoad(files[i], i);
		}	
		
		
	}
	
	public static void parseAndLoad(String file, int i) throws IOException {
		
		/*
		 * ArrayList<Integer>[] cM; cM = new ArrayList[23]; for (int i=1;i<23;i++) {
		 * cM[i]= {1,2,3,4,5,6}; }
		 */
		
		String s="";
		FileReader fr;
		StringBuilder str = new StringBuilder();
		String a= "cM[" +i+"] = new int [] {0,";
		str.append(a);
		
		try {
			fr = new FileReader(file);
			BufferedReader bf = new BufferedReader(fr);
			while ( (s= bf.readLine()) != null) {
				String[] words = s.split("\\s");
				if (words[1].contentEquals("SNP")) {
					int cm= (int )Double.parseDouble(words[6]);
					int x=cm+1;
					while (cm != x && (s= bf.readLine()) != null) {
						String[] words1 = s.split("\\s");
						int cms= (int )Double.parseDouble(words1[6]);
						cm=cms;
					}
					if (s!=null) {
						String[] w = s.split("\\s");
						str.append(w[5]);
						str.append(",");
						//System.out.println("RSID : " + w[0] +" || Location : "+ w[5] +" || CM : "+cm);
					}
				}
				
			}
			str.append("};");
			System.out.println("\n"+str);
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
		
	

}
