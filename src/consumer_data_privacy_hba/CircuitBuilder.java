package consumer_data_privacy_hba;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class CircuitBuilder {

	public CircuitBuilder() {
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
		
		buildCircuit(10);
		
	}
	
	public static int nextPowerOf2 (int num) {
		
		if (num <= 0 || num > 65536)
			return -1;
		if (num > 0 && ((num & (num - 1)) == 0))
			return num;
		else 
			return (int) Math.pow(2, (int) ( (Math.log(num) / Math.log(2)) + 1));
			
	}
	
	@SuppressWarnings("unused")
	public static void buildCircuit(int snips) {
		
		int circuitSize= nextPowerOf2(snips);
		int i, alice =0, bob= 3*snips , wires=6*snips, num_gates = 2;
		int pad = wires+1;
		int cin =pad;
		
		
		Queue<Integer> lsb = new LinkedList<>();
		int inter[] = new int[circuitSize];
		
		
		//System .out.println("number of snips : "+snips);
		//System .out.println("circuit size " +circuitSize);
		FileWriter fw,fw1;
		try {
			/*
			 * First 2 lines of circuit to produce the zero output Carry in wire.
			 * Subsequently this wire will be used for padding the circuit with 
			 * 000 encoded genotype for heterozygotes to produce matches.
			 */
			String lines="1 1 0 "+wires +" INV\n2 1 0 "+ wires +" "+(++wires)+" AND\n";
			wires++;
			//System.out.println(lines);
			fw = new FileWriter("input/Adders.txt");
			BufferedWriter bw1  = new BufferedWriter(fw);
			fw.write(lines);
			
			
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
				//System.out.println("Half Adder Output : " );
				//msb.add(wires-2);
				//lsb.add(wires-1);
				num_gates+=2;
				fw.write(lines);
				//System.out.println(lines);
			}
			
			if(circuitSize>=4)
				circuitSize/=4;
			else {
				bw1.close();
				fw.close();
				return;
			}
			//System.out.println("\t\t Inter: \t"+Arrays.toString(inter));
			/*
			 * Implementing n-bit Adder tree
			 */
			int nBitAdder=2,carry=0, newIndex=0;
			int bet [] = new int[100];
			while(circuitSize!=0) {
				index=0;
				newIndex=0;
				for (i=0; i<circuitSize; i++) {
					/*
					 * System.out.println("\t\t\t\tcircuitSize : " +circuitSize);
					 * System.out.println("\t\t\t\tcircuitSize : " +i);
					 * System.out.println("\t\t\t\tnBitAdder : "+ nBitAdder);
					 * System.out.println("\t\t Inter: \t"+Arrays.toString(inter));
					 * System.out.println("\t index: " + index); System.out.println("\t newIndex: "
					 * + newIndex);
					 */
					//index=0;
					//newIndex=0;
					for(int j=0;j<nBitAdder;j++) {
						//System.out.println("\t\t\t\tFULL ADDER : " +j);
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
						//System.out.println(lines);
						//System.out.println("\t carry: " + carry);
						fw.write(lines);
						num_gates+=8;
						//System.out.println("\t index: " + index);
						//System.out.println("\t newIndex: " + newIndex);
					}
					index+=nBitAdder;
				}
				nBitAdder++;
				circuitSize/=2;
			}
			//System.out.println("\t\t\t\tnBitAdder: " +nBitAdder);
			//System.out.println("MSB : "+(msb));
			//System.out.println("LSB : "+(lsb));
			//System.out.println("\t\t Inter: \t"+Arrays.toString(inter));
			
			/*
			 * >=T2 circuit
			 */
			
			int t2= Math.round( (float)snips/1000);
			int adderOutput[] = new int[nBitAdder];
			index=0;
			for (i=0; i<nBitAdder;i++) {
				adderOutput[index++]=inter[i];
			}
			//System.out.println("Values of Snips and t2 : " +snips+"  "+t2 );
			
			//System.out.println("Values output : " +nBitAdder );
			//System.out.println("\t\t adderOutput: \t"+Arrays.toString(adderOutput));
			
			
			//Calling solve method for the >=t2 circuit expression
			String s=solve (t2+1,adderOutput.length);
			
			//System.out.println(s);
			
			//Splitting the solution   V9 OR V8 OR V7 OR V6 OR V5 OR V4 OR V3 OR  ( V2 AND ( V1 OR V0 ) ) 
		    String[] words = s.split("\\s+");
		    boolean ands=false; 
		    int braces=0;
		    //System.out.println("words.length " +words.length);
		    
			/*
			 * for (int x =0 ; x<words.length;x++) System.out.println(x+" : "+words[x]);
			 */
			
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
		    	
		    //System.out.println("Start :  " + start + "  || End : " +end);
			
			
		    lines="";
			//int wires=0;
			
			//Converting OR to NAND gates.
			//Queue<Integer> lsb = new LinkedList<>();
			for ( i=start;i>=end; i--) {
				//lines += "2 1 " + i + " " + i + " "  + (wires++) + " AND\n";
				lines += "1 1 " + adderOutput[i] + " " + (wires++) + " NOT\n";
				lsb.add(wires-1);
			}
			//fw.write(lines);
			//while(lsb.size()!=0)
				//System.out.println(lsb.remove());
		    
			int input1 , input2 ;
		    
			//Converging the NAND GATE outputs to ANDs
			while(lsb.size() !=1) {
				if (lsb.size() <= 0) {
					bw1.close();
					return;
				}
					
				input1 = lsb.remove();
				input2 = lsb.remove();
				lines += "2 1 " + input1 + " " + input2 + " " + (wires++) + " AND\n";
				lsb.add(wires-1);
			}
			//System.out.println("\n\n"+lines);
			fw.write(lines);
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
					if (words[index].equals("(")) {
						
						//System.out.println(index);
						openBraces.push(index);
					}
					index++;	
				}	
				
				//finding indexes of closedBraces
				while(index != words.length) 
					closedBraces.add(index++);
		    
		    
				//System.out.println(openBraces);
				//System.out.println(closedBraces);
				
				input1=input2=-1;
				String operator="";
				boolean flag1=false;
				boolean flag2=false;
				
				//solving for each set of braces
				while(openBraces.size()!=0 && closedBraces.size()!=0) {
					int front = openBraces.pop();
					int back = closedBraces.remove();
		    
					//System.out.print("\n\n "+counter+" : "+front+" " + back + " :  ");	
					
					//printing contents of each braces
//					for(int i=front;i<=back;i++) {
//						if (!words[i].equals("(") && !words[i].equals(")"))
//						System.out.print(words[i] + " ");
//					}
					
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
										//System.out.println();
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
							
							//System.out.print("\n\t\t index :"  +i+ "  : Input1 : "+one +" :  Input2 : "+two + " :   Operator : "+operator);
						}
						
						//System.out.println("\n Processed : "+processed);
					}
					
						
					//System.out.print("\n****Final Ones   : Input1 : "+input1 +" :  Input2 : "+input2 + " :   Operator : "+operator);
					lines="";
					
					if (counter==0 && input2==-1)
						continue;
					if (input1==-1 && input2==-1)
						continue;
					if (input1>=0 && input2>=0 && !operator.isEmpty()) {
						if (operator.equals("AND")) {
							lines="2 1 " + input1 + " " + input2 + " " + (wires++) + " AND\n";
							lsb.add(wires-1);
							fw.write(lines);
						}
						if (operator.equals("OR") ) {
							lines="2 1 " + input1 + " " + input1 + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1) + " " + (wires++) + " NOT\n";
							lines+="2 1 " + input2 + " " + input2 + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1) + " " + (wires++) + " NOT\n";
							lines+="2 1 " + (wires-3) + " " + (wires-1) + " " + (wires++) + " AND\n";
							lines+="1 1 " + (wires-1) + " " + (wires++) + " NOT\n";
							lsb.add(wires-1);
							fw.write(lines);
						}
							
					}
					if (input1>=0 && input2==-1 && !lsb.isEmpty() && !operator.isEmpty()) {
						if (operator.equals("AND")) {
							lines+="2 1 "+ input1 + " " + (lsb.remove()) + " " + (wires++) + " AND\n";
							lsb.add(wires-1);
							fw.write(lines);
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
							fw.write(lines);
						}
					}
					//System.out.println("\n\n"+lines);
					//System.out.println("\n\n"+lsb);
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
					fw.write(lines);
				}
					
				//System.out.println("\n" +lines);	
					
				}else {
					lines = "1 1 " +(wires-1) + " " + (wires++) + " NOT\n";
					//System.out.println(lines);
					fw.write(lines);
				}
		
			/*
			 * 
			 * End of t2 circuit
			 * 
			 * 
			 * 
			 */
		    
		    
			
			
			
			fw.flush();
			fw.close();
			bw1.close();
			fw1 = new FileWriter("input/Adders.txt",true);
			BufferedWriter bw  = new BufferedWriter(fw1);
			num_gates= wires - 6* snips;
			lines=num_gates+ " " +  wires +"\n" +
					(snips*3) + " " + (snips*3) + " " + "1" + "\n\n";
			fw1.append(lines);
			fw1.close();
			bw.close();
			//System.out.println(" Wires " + wires + " Gates " + num_gates);
			//System.out.println(lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		 * System.out.println("Status of Queue\n"); System.out.println(lsb.size());
		 * while(lsb.size()!=0) System.out.println(lsb.remove());
		 */
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
			
		
		//System.out.println(line);
		//System.out.println(Arrays.toString(last));
		
		return line;
	}
}















