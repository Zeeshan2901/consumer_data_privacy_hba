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
		String test_file= "input/case3_test_results.csv";
		String data_file= "input/case3_data_characteristics.csv";
		for (int i=1; i<=14;i++) {
			for (int j= i+1; j<=15; j++) {
				//Thread.sleep(15000);
				System.out.println("\n\n\n\t\t\t\t\tExecution of "+i +" and "+j);
				
				Server_Test obj = new Server_Test(loc+i+ext);
				Client_Test obj1 = new Client_Test((loc+j+ext), test_file, data_file );
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
