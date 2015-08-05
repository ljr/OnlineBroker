package br.usp.icmc.lasdpc.cloudsim.examples.DCBroker;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.Monitor;
import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;

public class DatacenterBroker extends OnlineBroker {

	public List<Cloudlet> cloudletReceivedList;
	
	public DatacenterBroker(String name, int sampleTime, Monitor monitor,
			Effector effector, Demand demand, Capacity capacity)
			throws Exception {
		super(name, sampleTime, monitor, effector, demand, capacity);
		
		cloudletReceivedList = new ArrayList<Cloudlet>();
	}

	public DatacenterBroker(String name) throws Exception {
		this(name, 10, 
				new DCMonitor(), 
				new DCEffector(), 
				new BatchDemand(), 
				new BatchCapacity()
		);
	}
	
	
	@Override
	public void processEvent(SimEvent ev) {
		super.processEvent(ev);
		
		switch (ev.getTag()) {
			case CloudSimTags.CLOUDLET_RETURN:
				cloudletReceivedList.add((Cloudlet) ev.getData());
				break;
				
			case CloudSimTags.VM_CREATE_ACK:
				((DCMonitor) getMonitor()).incVmsAcks();
				break;
			default:
				
		}
	}
	
	
	public void submitVmList(List<Vm> vmlist) {
		getMonitor().getVmList().addAll(vmlist);
	}

	public void submitCloudletList(List<Cloudlet> cloudletList) {
		getMonitor().getCloudletList().addAll(cloudletList);
	}

	public List<Cloudlet> getCloudletReceivedList() {
		return cloudletReceivedList;
	}

}
