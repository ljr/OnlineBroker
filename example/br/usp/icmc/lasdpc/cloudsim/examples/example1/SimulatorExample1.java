package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;

public class SimulatorExample1 {

	public static void main(String[] args) throws Exception {

		
		 /*
		  * CLOUDSIM REQUIRED INITIALIZATION
		  */
		int num_user = 1; // number of cloud users
		Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
		boolean trace_flag = false; // trace events
		CloudSim.init(num_user, calendar, trace_flag);

		
		/*
		 * CREATE A DATACENTER
		 */
		newDatacenter("DC1");
		
		
		/*
		 * CRATE A BROKER
		 */
		int sampleTime = 10;
		new OnlineBroker("mybroker", sampleTime, new MyMonitor(), 
				new MyEffector(), new MyCapacity(), new MyDemand());
		
		
		/*
		 * START THE SIMULATION
		 */
		CloudSim.startSimulation();
		
		
		System.out.println("SimulatorExample1 ended.");
	}

	/**
	 * Create a simple datacenter, {@link org.cloudbus.cloudsim.examples.CloudSimExample1#createDatacenter createDatacenter}
	 * 
	 * @param name
	 * @return
	 */
	private static Datacenter newDatacenter(String name) {
		List<Host> hostList = new ArrayList<Host>();
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000;

		peList.add(new Pe(0, new PeProvisionerSimple(mips)));

		int hostId = 0;
		int ram = 2048; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000;

		hostList.add(
			new Host(
				hostId,
				new RamProvisionerSimple(ram),
				new BwProvisionerSimple(bw),
				storage,
				peList,
				new VmSchedulerTimeShared(peList)
			)
		); // This is our machine

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this
										// resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>(); // we are not adding SAN
													// devices by now

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

}
