package consumer_data_privacy_hba;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void genecount(ArrayList<GenotypedData>[] checkGenes) {
		
		
		
		
		/*int homo=0, hetero=0, totalHomo=0, totalHetero=0;
		 
		for (int i =1 ; i<=22; i++) {
			homo=hetero=0;
			for(int j=0; j< checkGenes[i].size(); j++) {
				GenotypedData obj= checkGenes[i].get(j);
				if (obj.gene1==obj.gene2)
					homo++;
				else
					hetero++;
			}
			System.out.println("\n\t Chromosome : "+i+" Homo : "+homo+" Hetero : "+hetero+
					"   % of Homozygotes : " +(homo*100/ (homo +hetero))+"%");
			totalHomo+=homo;
			totalHetero+=hetero;
		}
		System.out.println("\n\n\n\t Total Homo : "+totalHomo+" Total Hetero : "+ totalHetero+ 
				"  % of Homozygotes : " +(totalHomo*100/(totalHomo+totalHetero)));*/
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		int chromosome = 'M' & 0xF;
		System.out.println(chromosome);
		
		
		// TODO Auto-generated method stub
		
		//ConsumerDataPrivacyHBA a = new ConsumerDataPrivacyHBA();
		//ConsumerDataPrivacyHBA b = new ConsumerDataPrivacyHBA();
		
		/*
		//b.csvParser("input/mather_all.txt");
		long startTime = System.currentTimeMillis();
		
		//genecount(b.genes);
		try {
			String s="",lastline="";
			FileReader fr = new FileReader("input/non_matching_case_1_test_results.csv");
			BufferedReader bf = new BufferedReader(fr);
			while ( ( s= bf.readLine()) != null) {
				lastline=s;
			}
			System.out.println("yo : "+lastline);	
			System.out.println("yo : "+lastline);	
			String [] temp = lastline.split(",");
			int id = Integer.parseInt(temp[0]) + 1;
			System.out.println(id);
				
			FileWriter fw = new FileWriter("input/non_matching_case_1_test_results.csv", true);
		    BufferedWriter bw = new BufferedWriter(fw);
		    PrintWriter out = new PrintWriter(bw);		
		    //out.println("the text");
		    //out.println("more text");
		    out.close();
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
		Thread.sleep(29);
		long stopTime = System.currentTimeMillis();
	      long elapsedTime = stopTime - startTime;
	      System.out.println("Time : " +elapsedTime);*/
	}
}
