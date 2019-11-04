package consumer_data_privacy_hba;

public class GenotypedData implements Comparable<GenotypedData>{
	
	
	int location;
	String rsid;
	char gene1;
	char gene2;
	

	public GenotypedData() {
		location =-1;
		rsid = null;
		gene1 = 'z';
		gene2 = 'z';
	}
	
	public GenotypedData(GenotypedData obj) {
		this.location = obj.location;
		this.rsid = obj.rsid;
		this.gene1 = obj.gene1;
		this.gene2 = obj.gene2;
	}
	
	public void display(GenotypedData obj) {
		System.out.println("\tLocation : " + obj.location + " RSID : "+ obj.rsid + " Genotype : " +obj.gene1+obj.gene2);	
	}

	@Override
	public int compareTo(GenotypedData g) {
		return (location < g.location) ? -1 : ((location == g.location) ? 0:1);
	}
	
	@Override
	public String toString() {
		return "Location:  " + this.location + 
				", RSID: " + this.rsid +
				", Genotype: " +this.gene1+this.gene2 +" || ";
	}
	
	public int getLocation() {
		return this.location;
	}
	
	public String getRSID() {
		return this.rsid;
	}
	
	public char getGene1() {
		return (this.gene1);
	}
	
	public char getGene2() {
		return (this.gene2);
	}

}
