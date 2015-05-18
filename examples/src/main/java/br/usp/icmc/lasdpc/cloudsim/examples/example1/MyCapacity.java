package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.Event;
import br.usp.icmc.lasdpc.cloudsim.Ack;

public class MyCapacity extends Capacity {

	
	@Override
	public List<Event> update(Map<Integer, Map<String, List<Object>>> values) {
		
		// TODO: always clear events before calling it.
		events.clear();
				
		for (int k : values.keySet()) {
			switch (k) {
			case CloudSimTags.EXPERIMENT:
				
				setCapacity((double) values.get(k).get("CLOCK").get(0));
				
				break;

			case CloudSimTags.VM_CREATE_ACK:
			case CloudSimTags.VM_DESTROY_ACK:
				
				processVmAck(values.get(k).get("ACK"), k == CloudSimTags.VM_CREATE_ACK);
				
				break;
			
			default:
				break;
			}
		}
		
		return events;
	}
	
	private void setCapacity(double clock) {
		if (clock == 10) { 
			createVM();
		} else if (clock == 900) {
			destroyVM();
		}
	}

	private void processVmAck(List<Object> acks, boolean isCreate) {
		for (Object v : acks) {
			Ack va = (Ack) v;
			Log.printConcatLine(CloudSim.clock(), ": ", va);
			
			if (va.getSuccess() == CloudSimTags.TRUE) {
				setVMDatacenter(va.getId(), 
						isCreate ? va.getDatacenterId() : null);
			}
		}		
	}

	private void destroyVM() {
		for (int vm : vms.keySet()) {
			events.add(new Event(getDatacenter(vm), 
					CloudSimTags.VM_DESTROY_ACK, getVm(vm)));
		}		
	}

	private void createVM() {
		Vm vm = newVm(); 
		create(vm);
		events.add(new Event(chooseDataCenter(), CloudSimTags.VM_CREATE_ACK, vm));
	}

	private int chooseDataCenter() {
		return mybroker.getDcs().get(0);
	}

	private Vm newVm() {
		// VM description
		int vmid = 0;
		int mips = 1000;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		// create VM
		return new Vm(vmid, mybroker.getId(), mips, pesNumber, ram, bw, size, 
				vmm, new CloudletSchedulerTimeShared());
	}

}
