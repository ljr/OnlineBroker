package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.Calendar;

import org.cloudbus.cloudsim.core.CloudSim;

import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;

public class SimulatorExample1 {

	public static void main(String[] args) throws Exception {
		int num_user = 1; // number of cloud users
		Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
		boolean trace_flag = false; // trace events
		CloudSim.init(num_user, calendar, trace_flag);

		
		int sampleTime = 10;
		new OnlineBroker("mybroker", sampleTime, new MyMonitor(), 
				new MyEffector(), new MyCapacity(), new MyDemand());
		
		
		CloudSim.startSimulation();
		
		
		System.out.println("SimulatorExample1 ends.");
	}

}
