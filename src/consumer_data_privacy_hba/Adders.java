package consumer_data_privacy_hba;

import java.io.FileWriter;
import java.io.IOException;

public class Adders {

	public Adders() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		FileWriter fw;
		try {
			fw = new FileWriter("input/Adders.txt");
		
		int n=32;
		int x=0, y=0, c=0, wire= 2*n ;
		int intermediate[] = {0,0,0,0,0,0,0,0};
		
		int arr[]= {8*n -6 , 10*(n-1) +4, n+1};
		String start= arr[0]+ " "+ arr[1] + "\n" + n + " " + n + " " + arr[2] + "\n" + "\n";
		fw.write(start);
		
		
		
		
		for (int i=0; i<n;i++){
			int index=0;
			x=i;
			y=x+n;
			String line="";
			
			if (i ==0) {
				line+="2 1 "+ x +" "+ y +" "+ wire +" "+ "XOR";
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				line ="";
				line+="2 1 "+ x +" "+ y +" "+ ++wire +" "+ "AND";
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
			}
			else {
				line+="2 1 "+ x +" "+ y +" "+ wire +" "+ "XOR"; 
				intermediate[index++]=wire;
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				line ="";
				line+="2 1 "+ c + " "+ wire + " "+ ++wire +" "+ "XOR";
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				line="";
				line+="2 1 "+ x + " " + y + " " + ++wire +" "+ "AND";
				intermediate[index++]=wire;
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				
				line ="";
				line+="2 1 "+ c + " " + intermediate[0] + " " + ++wire +" "+ "AND";
				intermediate[index++]=wire;
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				line ="";
				line+="1 1 "+ intermediate[2] + " " + ++wire +" "+ "INV";
				intermediate[index++]=wire;
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				line ="";
				line+="1 1 "+ intermediate[1] + " " + ++wire +" "+ "INV";
				intermediate[index++]=wire;
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				line ="";
				line+="2 1 "+ intermediate[3] + " " + intermediate[4] + " " + ++wire +" "+ "AND";
				intermediate[index++]=wire;
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				line ="";
				line+="1 1 "+ intermediate[5] + " " + ++wire +" "+ "INV";
				fw.write(line);
				fw.write("\n");
				System.out.println(line);
				
				
			}
			c=wire;
			wire++;
			
		}
		System.out.println((n+1)+" , "+n+" , "+(n+1)+" , "+wire + " , "+c);
		System.out.println ("\n"+start);
		fw.close();
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
