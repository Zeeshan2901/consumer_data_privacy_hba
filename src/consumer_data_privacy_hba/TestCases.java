package consumer_data_privacy_hba;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestCases {
	
	 
	
	public static void main (String [] a) throws InterruptedException, IOException {
		
		String loc = "test_files/case3/";
		String ext = ".txt";
		for (int i=1; i<=14;i++) {
			for (int j= i+1; j<=15; j++) {
				//Thread.sleep(15000);
				System.out.println("\n\n\n\t\t\t\t\tExecution of "+i +" and "+j);
				
				HBA_Server_V2 obj = new HBA_Server_V2(loc+i+ext);
				HBA_Client_V2 obj1 = new HBA_Client_V2(loc+j+ext);
				System.out.println("Objects Created");
				
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
				System.out.println("Waiting for 500ms");
				Thread.sleep(100);
			}
		}	
	}
}
