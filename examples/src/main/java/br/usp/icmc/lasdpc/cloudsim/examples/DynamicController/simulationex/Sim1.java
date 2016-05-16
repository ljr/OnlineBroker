package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController.simulationex;

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
import br.usp.icmc.lasdpc.cloudsim.examples.DynamicController.AllocationEffector;
import br.usp.icmc.lasdpc.cloudsim.examples.DynamicController.PICapacity;
import br.usp.icmc.lasdpc.cloudsim.examples.DynamicController.PerformanceMonitor;
import br.usp.icmc.lasdpc.cloudsim.examples.DynamicController.StepDemand;

public class Sim1 {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		int num_user = 1;
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false;
		CloudSim.init(num_user, calendar, trace_flag);
		Datacenter datacenter0 = createDatacenter("Datacenter_0");

		
		double lambdaBefore = 20;
		double lambdaAfter = 10;
		long muBefore = 14;
		long muAfter = 7;
		double changeTime = 1000;
		long seed = 12345;
		int vmsAtStart = 7;
		double kp = 8.5446;
		double ki = -0.85535;
		double kd = 0;
		
		OnlineBroker ob = new OnlineBroker("Broker", 10, 
				new PerformanceMonitor(), 
				new AllocationEffector(), 
				new StepDemand(seed, StepDemand.newWorkload(lambdaBefore, 
						lambdaAfter, muBefore, muAfter, changeTime)), 
				new PICapacity(vmsAtStart, .7, kp, ki, kd)
		);
		
		CloudSim.startSimulation();
		CloudSim.stopSimulation();
	}

	
	/**
	 * Creates the datacenter.
	 *
	 * @param name the name
	 *
	 * @return the datacenter
	 */
	private static Datacenter createDatacenter(String name) {

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		// our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 1000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

		// 4. Create Host with its id and list of PEs and add them to the list
		// of machines
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

		// 5. Create a DatacenterCharacteristics object that stores the
		// properties of a data center: architecture, OS, list of
		// Machines, allocation policy: time- or space-shared, time zone
		// and its price (G$/Pe time unit).
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

		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}
}
