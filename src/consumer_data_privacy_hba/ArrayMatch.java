package consumer_data_privacy_hba;

import java.util.ArrayList;
import java.util.List;


/*
 * Outputs
 
 
 A : [0, 1, 4, 5, 7, 10, 12, 15, 18, 20, 21, 23, 23, 24, 25, 26, 36, 50, 100, 1000, 1100000]
 B : [0, 1, 2, 3, 4, 6, 7, 8, 9, 12, 13, 14, 15, 18, 19, 20, 22, 22, 22, 22, 24, 36, 89, 100, 110]

 A : [0, 1, 4, 7, 12, 15, 18, 20, 24, 36, 100]
 B : [0, 1, 4, 7, 12, 15, 18, 20, 24, 36, 100]

 */

public class ArrayMatch {

	public ArrayMatch() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		
		
		List<Integer> a = new ArrayList<>(List.of(0,1,4,5,7,10,12,15,18,20,21,23,23,24,25,26,36,50,100,1000,1100000));
		List<Integer> b = new ArrayList<>(List.of(0,1,2,3,4,6,7,8,9,12,13,14,15,18,19,20,22,22,22,22,24,36,89,100,110));

		int i,j,k;
		
		System.out.print("\n A : "+a);
		
		System.out.print("\n B : "+b);
		
		for (i=0,j=0; i<a.size() && j<b.size();i++,j++) {
			
			if ( a.get(i) != b.get(j) ) {
				if ( a.get(i) > b.get(j) ) {
					for(k=j;k<b.size();k++) {
						if ( a.get(i) > b.get(k)) { 
							b.remove(k);
							k--;
						}
						else if ( a.get(i) == b.get(k) ) {
							j=k;
							break;
						}
						else if ( a.get(i) < b.get(k) ) {
							j=i;
							j--;
							i--;
							break;
						}
					}
				}
				else if  ( a.get(i) < b.get(j) ) {
					for(k=i;k<a.size();k++) {
						if ( a.get(k) < b.get(j)) {
							a.remove(k);
							k--;
						}
						else if ( a.get(k) == b.get(j) ) {
							i=k;
							break;
						}
						else if ( a.get(k) > b.get(j) ) {
							i=j;
							j--;
							i--;
							break;
						}	
					}
					
				}
			}
		}
		
		if (a.size() > b.size()) 
			a.subList(b.size(), a.size()).clear();
		else if (b.size() > a.size())
			b.subList(a.size(), b.size()).clear();
		
		System.out.print("\n\n A : "+a);
		System.out.print("\n B : "+b);
		
		
	}

}
