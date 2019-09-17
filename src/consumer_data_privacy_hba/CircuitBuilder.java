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
		
		buildCircuit(11);
		
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
		int cin =pad;
		
		Queue<Integer> lsb = new LinkedList<>();
		Queue<Integer> msb = new LinkedList<>();
		int inter[] = new int[circuitSize];
		
		
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
				System .out.println("circuit size " +i);
				lines="";
				if (i< snips) {
					lines=DNAMatch(alice, bob, wires, false);
					alice+=3;
					bob+=3;
					wires+=8;
					num_gates+=8;
					fw.write(lines);
					lsb.add(wires-1);
					
				}
				else {
					lines=DNAMatch(pad, pad, wires, true);
					wires+=8;
					num_gates+=8;
					fw.write(lines);
					lsb.add(wires-1);
				}
			}
			
			/*
			 * Half Adder Circuit
			 */
			int a,b, index=0;
			for (i=0; i<circuitSize/2; i++) {
				lines="";
				a=lsb.remove();
				b=lsb.remove();
				lines+="2 1 "+ a + " " + b + " " + wires++ + " " + "AND\n" + 
					   "2 1 "+ a + " " + b + " " + wires++ + " " + "XOR\n";
				inter[index++]=wires-1;		// Sum output --- lsb
				inter[index++]=wires-2;		// Carry output --- msb
				System.out.println("Half Adder Output : " );
				//msb.add(wires-2);
				//lsb.add(wires-1);
				num_gates+=2;
				fw.write(lines);
				System.out.println(lines);
			}
			
			if(circuitSize>=4)
				circuitSize/=4;
			else {
				bw1.close();
				fw.close();
				return;
			}
			System.out.println("\t\t Inter: \t"+Arrays.toString(inter));
			/*
			 * Implementing n-bit Adder tree
			 */
			int nBitAdder=2,carry=0, newIndex=0;
			int bet [] = new int[100];
			while(circuitSize!=0) {
				index=0;
				newIndex=0;
				for (i=0; i<circuitSize; i++) {
					System.out.println("\t\t\t\tcircuitSize : " +circuitSize);
					System.out.println("\t\t\t\tcircuitSize : " +i);
					System.out.println("\t\t\t\tnBitAdder : "+ nBitAdder);
					System.out.println("\t\t Inter: \t"+Arrays.toString(inter));
					System.out.println("\t index: " + index);
					System.out.println("\t newIndex: " + newIndex);
					//index=0;
					//newIndex=0;
					for(int j=0;j<nBitAdder;j++) {
						System.out.println("\t\t\t\tFULL ADDER : " +j);
						lines="";
						lines="2 1 " + inter[index] + " " + inter[index+nBitAdder] + " " + wires++ + " XOR\n";
						bet[0]=wires-1;
						lines+="2 1 " + inter[index] + " " + inter[index+nBitAdder] + " " + wires++ + " AND\n";
						lines+="1 1 " + (wires -1) + " " + wires++ + " INV\n";
						bet[1]=wires-1;
						if (j==0)
							lines+="2 1 " + cin + " " + bet[0] + " " + wires++ + " AND\n";
						else
							lines+="2 1 " + carry + " " + bet[0] + " " + wires++ + " AND\n";
						lines+="1 1 " + (wires -1) + " " + wires++ + " INV\n";
						lines+="2 1 " + bet[1] + " " + (wires-1) + " " + wires++ + " AND\n";
						lines+="1 1 " + (wires -1) + " " + wires++ + " INV\n";
						
						if (j==0)
							lines+="2 1 " + cin + " " + bet[0] + " " + wires++ + " XOR\n";
						else 
							lines+="2 1 " + carry + " " + bet[0] + " " + wires++ + " XOR\n";
						index++;
						inter[newIndex++]= wires-1;
						carry=wires-2;
						if (j==nBitAdder-1)
							inter[newIndex++]=carry;
						System.out.println(lines);
						System.out.println("\t carry: " + carry);
						fw.write(lines);
						num_gates+=8;
						System.out.println("\t index: " + index);
						System.out.println("\t newIndex: " + newIndex);
					}
					index+=nBitAdder;
				}
				nBitAdder++;
				circuitSize/=2;
			}
			
			System.out.println("MSB : "+(msb));
			System.out.println("LSB : "+(lsb));
			System.out.println("\t\t Inter: \t"+Arrays.toString(inter));
			
			
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
		System.out.println(lsb.size());
		while(lsb.size()!=0)
			System.out.println(lsb.remove());
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















