package consumer_data_privacy_hba;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class CircuitBuilder {

	public CircuitBuilder() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		//buildCircuit(11);
		
		for (int i=0; i< 1024; i++)
		System.out.println( "Value of " +i+"  Pow :  " +nextPowerOf2(i));
	}
	
	public static int nextPowerOf2 (int num) {
		
		if (num <= 0 || num > 65536)
			return -1;
		if (num > 0 && ((num & (num - 1)) == 0))
			return num;
		else 
			return (int) Math.pow(2, (int) ( (Math.log(num) / Math.log(2)) + 1));
			
	}
	
	public static void buildCircuit(int snips) {
		
		int circuitSize= nextPowerOf2(snips);
		int i, alice =0, bob= 3*snips , wires=6*snips, num_gates = 2;
		int pad = wires+1;
		
		Queue<Integer> DNAResult = new LinkedList<>();
		
		System .out.println("number of snips : "+snips);
		System .out.println("circuit size " +circuitSize);
		FileWriter fw,fw1;
		try {
			/*
			 * First 2 lines of circuit to produce the zero output Carry in wire.
			 * Subsequently this wire will be used for padding the circuit with 
			 * 000 encoded genotype for heterozygotes to produce matches.
			 */
			String lines="1 1 0 "+wires +" INV\n2 1 0 "+ wires +" "+(++wires)+" AND\n";
			wires++;
			System.out.println(lines);
			fw = new FileWriter("input/Adders.txt");
			BufferedWriter bw1  = new BufferedWriter(fw);
			fw.write(lines);
			/*
			 * Creating the circuit for #snips SNP DNA matching Circuit 
			 */
			for (i =0; i<circuitSize; i++) {
				lines="";
				if (i< snips) {
					lines=DNAMatch(alice, bob, wires, false);
					alice+=3;
					bob+=3;
					wires+=8;
					num_gates+=8;
					fw.write(lines);
					DNAResult.add(wires-1);
					
				}
				else {
					lines=DNAMatch(pad, pad, wires, true);
					wires+=8;
					num_gates+=8;
					fw.write(lines);
					DNAResult.add(wires-1);
				}
			}
			
			/*
			 * Half Adder Circuit
			 */
			int a,b;
			for (i=0; i<circuitSize/2; i++) {
				lines="";
				a=DNAResult.remove();
				b=DNAResult.remove();
				lines+="2 1 "+ a + " " + b + " " + wires++ + " " + "AND\n" + 
					   "2 1 "+ a + " " + b + " " + wires++ + " " + "XOR\n";
				num_gates+=2;
				fw.write(lines);
			}
			fw.flush();
			fw.close();
			bw1.close();
			fw1 = new FileWriter("input/Adders.txt",true);
			BufferedWriter bw  = new BufferedWriter(fw1);
			lines=num_gates+ " " +  wires +"\n" +
					(snips*3) + " " + (snips*3) + " " + circuitSize + "\n\n";
			fw1.append(lines);
			fw1.close();
			bw.close();
			System.out.println(" Wires " + wires + " Gates " + num_gates);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Status of Queue\n");
		System.out.println(DNAResult.size());
		while(DNAResult.size()!=0)
			System.out.println(DNAResult.remove());
	}

	
	public static String DNAMatch(int user1, int user2, int wire , boolean isPadding) {
		String  line="";
		int last [] = new int[7];
		int index=0;
		if (isPadding == false) {
			line = "2 1 "+ user1++ + " " + user2++ + " " + wire++ + " AND\n";
			last[index++]=wire-1;
			line+= "2 1 "+ user1++ + " " + user2++ + " " + wire++ + " XOR\n";
			last[index++]=wire-1;
			line+= "2 1 "+ user1++ + " " + user2++ + " " + wire++ + " XOR\n";
			last[index++]=wire-1;
		}
		else {
			line = "2 1 "+ user1 + " " + user2 + " " + wire++ + " AND\n";
			last[index++]=wire-1;
			line+= "2 1 "+ user1 + " " + user2 + " " + wire++ + " XOR\n";
			last[index++]=wire-1;
			line+= "2 1 "+ user1 + " " + user2 + " " + wire++ + " XOR\n";
			last[index++]=wire-1;
			
		}
			
		line+= "1 1 "+ last[1] + " " + wire++ + " " + "INV\n";
		last[index++]=wire-1;
		line+= "1 1 "+ last[2] + " " + wire++ + " " + "INV\n";
		last[index++]=wire-1;
		line+= "2 1 "+ last[3] + " " + last[4] + " " + wire++ + " AND\n";
		last[index++]=wire-1;
		line+= "1 1 "+ last[5] + " " + wire++ + " " + "INV\n";
		last[index++]=wire-1;			
		line+= "2 1 "+ last[0] + " " + last[6] + " " + wire++ + " AND\n";
			
		
		System.out.println(line);
		System.out.println(Arrays.toString(last));
		
		return line;
	}
}















