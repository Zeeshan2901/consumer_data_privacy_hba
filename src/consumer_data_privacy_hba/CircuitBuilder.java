package consumer_data_privacy_hba;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class CircuitBuilder {
	private static int snips;
	public CircuitBuilder(int snp) {
		snips=snp;
	}
	
	static String solve( int thresh, int nbits) {
		String solution="";
		int i;
		
		if (thresh <= 0 || nbits < 1) return "TRUE";
		// bit positions bigger than threshold's biggest
		for (i = nbits-1; (i >= 0) && ((thresh & (0x1 << i)) == 0); --i)
		    solution += i+" OR ";
		if (i >= 0) {
		    String s = solve(thresh ^ (0x1 << i), i);
		    if (s.equals("TRUE"))
			solution += i;
		    else
			solution += (" ( "+i+" AND ( "+s+" ) ) ");
		}
		return solution;
	}
	    
    static boolean ifProcessed(Stack<Integer> stack, int element) { 
        return ( stack.search(element)==-1) ? false: true; 
    } 

	public static void main(String[] args) {
		CircuitBuilder obj = new CircuitBuilder(700);
		obj.buildCircuit();
		
	}
	
	public static int nextPowerOf2 (int num) {
		
		if (num <= 0 || num > 65536)
			return -1;
		if (num > 0 && ((num & (num - 1)) == 0))
			return num;
		else 
			return (int) Math.pow(2, (int) ( (Math.log(num) / Math.log(2)) + 1));
			
	}
	
	public void buildCircuit() {
		
		int circuitSize= nextPowerOf2(snips);
		int i, alice =0, bob= 3*snips , wires=6*snips, num_gates = 2;
		int pad = wires+1;
		int cin =pad;
		StringBuilder gates= new StringBuilder();
		Queue<Integer> lsb = new LinkedList<>();
		int inter[] = new int[circuitSize];
		
		FileWriter gatesWriter;
		try {
			/*
			 * First 2 lines of circuit to produce the zero output Carry in wire.
			 * Subsequently this wire will be used for padding the circuit with 
			 * 000 encoded genotype for heterozygotes to produce matches.
			 */
			String lines="1 1 0 "+wires +" INV\n2 1 0 "+ wires +" "+(++wires)+" AND\n";
			wires++;
			gates.append(lines);
			
			/*
			 * Creating the circuit for #snips SNP DNA matching Circuit 
			 */
			for (i =0; i<circuitSize; i++) {
				//System .out.println("circuit size " +i);
				lines="";
				if (i< snips) {
					lines=DNAMatch(alice, bob, wires, false);
					alice+=3;
					bob+=3;
					wires+=8;
					num_gates+=8;
					gates.append(lines);
					lsb.add(wires-1);
				}
				else {
					lines=DNAMatch(pad, pad, wires, true);
					wires+=8;
					num_gates+=8;
					gates.append(lines);
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
				num_gates+=2;
				gates.append(lines);
			}
			
			if(circuitSize>=4)
				circuitSize/=4;
			else {
				return;
			}
			/*
			 * Implementing n-bit Adder tree
			 */
			int nBitAdder=2,carry=0, newIndex=0;
			int bet [] = new int[100];
			while(circuitSize!=0) {
				index=0;
				newIndex=0;
				for (i=0; i<circuitSize; i++) {
					for(int j=0;j<nBitAdder;j++) {
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
						gates.append(lines);
						num_gates+=8;
					}
					index+=nBitAdder;
				}
				nBitAdder++;
				circuitSize/=2;
			}
			
			/*
			 * >=T2 circuit
			 */
			
			int t2= Math.round( (float)snips/1000);
			int adderOutput[] = new int[nBitAdder];
			index=0;
			for (i=0; i<nBitAdder;i++) 
				adderOutput[index++]=inter[i];
			
			//Calling solve method for the >=t2 circuit expression
			String s=solve (t2+1,adderOutput.length);
						
			//Splitting the solution   V9 OR V8 OR V7 OR V6 OR V5 OR V4 OR V3 OR  ( V2 AND ( V1 OR V0 ) ) 
		    String[] words = s.split("\\s+");
		    boolean ands=false; 
		    int braces=0;
			
		    //finding indexes for start, end of OR lines and starting of braces
		    int start=Integer.parseInt(words[0]);
		    int end=0;
		    for (i =1 ; i<words.length;i++) {	
		    	if (words[i].equals("(")) {
		    		braces=i;
		    		end= Integer.parseInt(words[i-2]);
		    		ands=true;
		    		break;
		    	}
		    	if (i==words.length -1 && !ands)
		    		end=Integer.parseInt(words[words.length -1]);	
		    }
		    lines="";
			
			//Converting OR to NAND gates.
			for ( i=start;i>=end; i--) {
				lines += "1 1 " + adderOutput[i] + " " + (wires++) + " NOT\n";
				lsb.add(wires-1);
			}
			
			int input1 , input2 ;
			//Converging the NAND GATE outputs to ANDs
			while(lsb.size() !=1) {
				if (lsb.size() <= 0) 
					return;	
				input1 = lsb.remove();
				input2 = lsb.remove();
				lines += "2 1 " + input1 + " " + input2 + " " + (wires++) + " AND\n";
				lsb.add(wires-1);
			}
			gates.append(lines);
			int finalWire=lsb.remove();
		    
			Stack<Integer> openBraces = new Stack<Integer>();
			Stack<Integer> processed = new Stack<Integer>();
			Queue<Integer> closedBraces = new LinkedList<>();
			Stack<Integer> gates1 = new Stack<Integer>();
			String one="",two="";
			int counter=0;
			
			//Solving the braces if there are (())
			//   V9 OR V8 OR V7 OR V6 OR V5 OR V4 OR V3 OR  ( V2 AND ( V1 OR V0 ) ) 
			if (ands) {	
				index=braces;
				//finding indexes of openBraces
				while(!words[index].equals(")")) {
					if (words[index].equals("(")) 
						openBraces.push(index);
					index++;	
				}	
				
				//finding indexes of closedBraces
				while(index != words.length) 
					closedBraces.add(index++);
		    	input1=input2=-1;
				String operator="";
				boolean flag1=false;
				boolean flag2=false;
				
				//solving for each set of braces
				while(openBraces.size()!=0 && closedBraces.size()!=0) {
					int front = openBraces.pop();
					int back = closedBraces.remove();
		    
					//for (V0) scenario
					if ((back-front)==2 && counter ==0) {
						input1=Integer.parseInt(words[front+1].replaceAll("[^0-9]", ""));
						one=words[front+1];
					}
					//rest of the cases
					else {
						for( i=front;i<=back;i++) {
							if (!words[i].equals("(") && !words[i].equals(")")) {
								if (!flag1 && !words[i].equals("AND") && !words[i].equals("OR") ) {
									int x =Integer.parseInt(words[i].replaceAll("[^0-9]", ""));
									if(!ifProcessed(processed,x)) {
										input1=x;
										one=words[i];
										flag1=true;
										processed.push(x);
									}
								}
								if (!flag2 && !words[i].equals("AND") && !words[i].equals("OR")) {
									int x=Integer.parseInt(words[i].replaceAll("[^0-9]", ""));
									if(!ifProcessed(processed,x)) {
										input2=x;
										two=words[i];
										flag2=true;
										processed.push(x);
									}
								}
								if ( (words[i].equals("AND") || words[i].equals("OR")) && !ifProcessed(gates1,i)) {
									operator=words[i];
									gates1.push(i);
								}
							}
						}
					}
					lines="";
					if (counter==0 && input2==-1)
						continue;
					if (input1==-1 && input2==-1)
						continue;
					if (input1>=0 && input2>=0 && !operator.isEmpty()) {
						if (operator.equals("AND")) {
							lines="2 1 " + input1 + " " + input2 + " " + (wires++) + " AND\n";
							lsb.add(wires-1);
							gates.append(lines);
						}
						if (operator.equals("OR") ) {
							lines="2 1 " + input1 + " " + input1 + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1) + " " + (wires++) + " NOT\n";
							lines+="2 1 " + input2 + " " + input2 + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1) + " " + (wires++) + " NOT\n";
							lines+="2 1 " + (wires-3) + " " + (wires-1) + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1) + " " + (wires++) + " NOT\n";
							lsb.add(wires-1);
							gates.append(lines);
						}	
					}
					if (input1>=0 && input2==-1 && !lsb.isEmpty() && !operator.isEmpty()) {
						if (operator.equals("AND")) {
							lines+="2 1 "+ input1 + " " + (lsb.remove()) + " " + (wires++) + " AND\n";
							lsb.add(wires-1);
							gates.append(lines);
						}
						if (operator.contentEquals("OR")) {
							int x= lsb.remove();
							lines="2 1 " + input1 + " " + input1 + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1 ) + " " + (wires++) + " " + " NOT\n";
							lines+="2 1 " + x + " " + x + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1 )  + (wires++) + " " + " NOT\n";
							lines+="2 1 " + (wires-3) + " " + (wires-1) + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1) + " " + (wires++) + " NOT\n";
							lsb.add(wires-1);
							gates.append(lines);
						}
					}
					flag1=false;
					flag2=false;
					one="";two="";
					input1=-1;input2=-1;
					counter++;
				}
				//Merging the output wires of OR gates and the braces	
				if (!lsb.isEmpty()) {
					int x = lsb.remove();
					lines="2 1 " + x + " " + x + " " + (wires++) + " AND\n";
					lines+="1 1 " + (wires-1 ) + " " + (wires++) + " " + " NOT\n";
					lines+="2 1 " + finalWire + " " + (wires-1) + " " + (wires++) + " AND\n";
					lines+="1 1 " + (wires-1) + " " + (wires++) + " NOT\n";
					gates.append(lines);
				}						
			}else {
				lines = "1 1 " +(wires-1) + " " + (wires++) + " NOT\n";
				gates.append(lines);
			}
			lines="1 1 " + (wires-1) + " " + (wires++) + " NOT";
			gates.append(lines);
			/*
			 * 
			 * End of t2 circuit
			 * 
			 * 
			 * 
			 */
			num_gates= wires - 6* snips;
			String header=num_gates+ " " +  wires +"\n" +
					(snips*3) + " " + (snips*3) + " " + "1" + "\n\n";
			gatesWriter=new FileWriter("/home/zeeshan/Desktop/hashing_based/temp_dir/circuit_"+snips+".txt");
			gatesWriter.write(header);
			gatesWriter.write(gates.toString());
			gatesWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		return line;
	}
}















