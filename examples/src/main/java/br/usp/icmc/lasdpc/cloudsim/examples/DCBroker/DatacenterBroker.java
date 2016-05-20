package br.usp.icmc.lasdpc.cloudsim.examples.DCBroker;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.Effector;
import br.usp.icmc.lasdpc.cloudsim.Monitor;
import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;
import br.usp.icmc.lasdpc.cloudsim.aux.Ack;

public class DatacenterBroker extends OnlineBroker {

	public List<Cloudlet> cloudletReceivedList;
	
	private int targetDc;
	private int targetVm;
	
	
	public DatacenterBroker(String name, int sampleTime, Monitor monitor,
			Effector effector, Demand demand, Capacity capacity)
			throws Exception {
		super(name, sampleTime, monitor, effector, demand, capacity);
		
		cloudletReceivedList = new ArrayList<Cloudlet>();
		targetDc = 0;
		targetVm = 0;
		getMonitor().add(DCMonitor.FIRST_SAMPLE, true);
	}

	public int getTargetDc() throws Exception {
		if (dcs.isEmpty()) {
			throw new Exception("No datacenter available.");
		}
		
		return dcs.get(targetDc++ % dcs.size());
	}
	
	public Vm getTargetVm() throws Exception {
		if (getMonitor().getVmManager().getRunning().isEmpty()) {
			throw new Exception("No VM available.");
		}
		
		int idx = targetVm++ % getMonitor().getVmManager().getRunning().size();
		
		return getMonitor().getVmManager().getCreatedList().get(idx);
	}
	
	public DatacenterBroker(String name) throws Exception {
		this(name, 10, 
				new DCMonitor(), 
				new DCEffector(), 
				new BatchDemand(), 
				new BatchCapacity()
		);
		
		((DCEffector) getEffector()).setBroker(this);
	}
	
	
	@Override
	public void processEvent(SimEvent ev) {
		super.processEvent(ev);
		
		switch (ev.getTag()) {
			case CloudSimTags.CLOUDLET_RETURN:
				cloudletReceivedList.add((Cloudlet) ev.getData());
				break;
				
			case CloudSimTags.VM_CREATE_ACK:
				processVmCreate(ev);
				break;
				
			case SAMPLE:
				if (getMonitor().allCloudletsProcessed()) {
					finishExecution();
				}
				getMonitor().add(DCMonitor.FIRST_SAMPLE, false);
				break;
			default:
				
		}
	}
	
	protected void processVmCreate(SimEvent ev) {
		Ack ack = new Ack((int []) ev.getData());
		
		getMonitor().getVmManager().newToRunning(ack);
		Log.printConcatLine(CloudSim.clock(), ": ", getName(), 
				": Creation of VM #", ack.getId(), " ", 
				ack.succeed()? "OK" : "FAILED",
				" in Datacenter #", ack.getDatacenterId());
	}
	
	
	public DCMonitor getMonitor() {
		return (DCMonitor) super.getMonitor();
	}
	
	public void submitVmList(List<Vm> vmlist) {
		getMonitor().getVmManager().addAll(vmlist);
	}

	public void submitCloudletList(List<Cloudlet> cloudletList) {
		getMonitor().getCloudletManager().add(cloudletList);
	}

	public List<Cloudlet> getCloudletReceivedList() {
		return cloudletReceivedList;
	}

	public void finishExecution() {
		sendNow(getId(), CloudSimTags.END_OF_SIMULATION);
	}

	/**
	 * Specifies that a given cloudlet must run in a specific virtual machine.
	 * 
	 * @param cloudletId ID of the cloudlet being bount to a vm
	 * @param vmId the vm id
	 * @pre cloudletId > 0
	 * @pre id > 0
	 * @post $none
	 */
	public void bindCloudletToVm(int cloudletId, int vmId) {
		getMonitor().getCloudletManager().getById(cloudletId).setVmId(vmId);
	}
	
}
