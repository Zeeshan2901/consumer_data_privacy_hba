package consumer_data_privacy_hba;

import java.io.IOException;
import java.util.ArrayList;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void genecount(ArrayList<GenotypedData>[] checkGenes) {
		
		int homo=0, hetero=0, totalHomo=0, totalHetero=0;
		 
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
				"  % of Homozygotes : " +(totalHomo*100/(totalHomo+totalHetero)));
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		ConsumerDataPrivacyHBA a = new ConsumerDataPrivacyHBA();
		ConsumerDataPrivacyHBA b = new ConsumerDataPrivacyHBA();
		
		b.csvParser("input/mather_all.txt");
		
		
		genecount(b.genes);
		

	}

}
