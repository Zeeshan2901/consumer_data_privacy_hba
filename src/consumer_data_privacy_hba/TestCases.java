package consumer_data_privacy_hba;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestCases {
	public static void main (String [] a) throws InterruptedException, IOException {
		
		/*
		 * Modify Below Variables Accordingly To Run Tests 
		 */
		String loc = "test_files/case3/";				//directory for test files for Server
		String loc1= "test_files/case3/";				//directory for test files for Server
		String csv = ".csv";							//extension of the test file
		String txt =".txt";								//extension of the test file
		String results= "input/TestCasesResults.csv";	//results are stored in this file 
		/*
		 * 
		 */
				
		for (int i=1; i<=1;i++) {
			for (int j= 2; j<=2; j++) {
				System.out.println("\n\n\n\t\t\t\t\tExecution of "+i +" and "+j);
				
				Server_Test obj = new Server_Test(loc1+"1"+txt);
				Client_Test obj1 = new Client_Test((loc+j+txt), results );
				System.out.println("Objects Created");
				System.out.println(i+"1"+j);

				Callable<Void> callable1 = new Callable<Void>(){
					public Void call() throws Exception{
						obj.run();
						return null;
					}
				};

				Callable<Void> callable2 = new Callable<Void>(){
					public Void call() throws Exception{
						obj1.run();
						return null;
					}
				};
   
				List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
				taskList.add(callable1);
				taskList.add(callable2);

				ExecutorService executor = Executors.newFixedThreadPool(2);
				System.out.println("Starting Calling Methods");
				try{
					//start the threads and wait for them to finish
					executor.invokeAll(taskList);
				}catch (InterruptedException ie){
					ie.printStackTrace();
				}
				System.out.println("Waiting for 100ms");
				Thread.sleep(100);
			}
		}	
	}
}
