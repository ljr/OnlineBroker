package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
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

	private static final int NUM_USER = 1; // number of cloud users
	
	public static void main(String[] args) throws Exception {
		
		
		 /*
		  * CLOUDSIM REQUIRED INITIALIZATION
		  */
		Calendar calendar = Calendar.getInstance(); // Calendar whose fields have been initialized with the current date and time.
		boolean trace_flag = false; // trace events
		CloudSim.init(NUM_USER, calendar, trace_flag);

		
		/*
		 * CREATE A DATACENTER
		 */
		newDatacenter("DC1");
		
		
		/*
		 * CRATE A BROKER
		 */
		int sampleTime = 10;
		OnlineBroker ob = new OnlineBroker("OnlineBroker", sampleTime, new MyMonitor(), 
				new MyEffector(), new MyDemand(), new MyCapacity());
		
		
		/*
		 * START THE SIMULATION
		 */
		CloudSim.startSimulation();
		
		
		printCloudletList(new ArrayList<Cloudlet>(ob.getDemand().getCloudlets()));
		
		
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
		int ram = 20480; // host memory (MB)
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

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Data center ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId()
						+ indent + indent + indent + cloudlet.getVmId()
						+ indent + indent
						+ dft.format(cloudlet.getActualCPUTime()) + indent
						+ indent + dft.format(cloudlet.getExecStartTime())
						+ indent + indent
						+ dft.format(cloudlet.getFinishTime()));
			}
		}
	}
	
}
