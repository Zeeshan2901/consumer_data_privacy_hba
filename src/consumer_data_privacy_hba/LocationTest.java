package consumer_data_privacy_hba;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

public class LocationTest {
	
	SortedMap <Integer,String> data =new TreeMap<Integer,String>();
	
	public void readFile(String location) {
		
		String line ="";
		try {
			FileReader fr = new FileReader(location);
			Scanner sc =new Scanner(fr);
			while (sc.hasNextLine()) {
				line=sc.nextLine();
				String[] row=line.split("\t");
				data.put(Integer.parseInt(row[2]),row[3]);				
			}			
			sc.close();
		}catch(IOException e) {
		System.out.println("Exception in reading at "+location);
		e.printStackTrace();
	}	
	}
	
	public void match(SortedMap <Integer,String> party) {
		int match=0,nonmatch=0,count=0;
		System.out.println("Now");
		for(Map.Entry<Integer, String> m:data.entrySet()){ 
			int key=m.getKey();
			String value=m.getValue();
			String value1=party.get(key);
			if (isMatch(value,value1))
				match++;
			else {
				nonmatch++;
				System.out.println(+key+" || "+value+" || "+value1);
			}
			count++;
		}
		
		System.out.println("\n Match : "+match+" || NonMatch : "+nonmatch+" || Count : "+count);
	}
	
	public void removeSpecial(SortedMap <Integer,String> party) {
		int []a=new int [1000]; int i=0;
		for(Map.Entry<Integer, String> m:data.entrySet()){ 
			int key=m.getKey();
			String value=m.getValue();
			
			if (value.contentEquals("--")) 
				a[i++]=key;
		}
		for (int j=0;j<i;j++) {
			data.remove(a[j]);
			party.remove(a[j]);
			System.out.println(a[j]);

		}
			
		
	}
	
	public boolean isHomozygous(String allele) {
		return (allele.charAt(0)==allele.charAt(1) );
	}
	
	public boolean isMatch(String s1, String s2) {
		if ( s1.charAt(0)==s2.charAt(0) || s1.charAt(0)==s2.charAt(1) || s1.charAt(1)==s2.charAt(0) || s1.charAt(1)==s2.charAt(1))
		
			return true;
		else return false;
		
	}
	
	public boolean isOppositeHomozygotes(String s1, String s2) {
		
		return true;
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LocationTest bob = new LocationTest();
		LocationTest alice = new LocationTest();
		
		bob.readFile("input/sister.txt");
		alice.readFile("input/dad.txt");
		
		alice.removeSpecial(bob.data);
		bob.removeSpecial(alice.data);
		alice.match(bob.data);
		
		

	}

}
