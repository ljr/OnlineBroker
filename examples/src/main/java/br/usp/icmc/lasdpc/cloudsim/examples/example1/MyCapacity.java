package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.Event;
import br.usp.icmc.lasdpc.cloudsim.VMAck;

public class MyCapacity extends Capacity {

	
	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		
		// TODO: always clear events before calling it.
		events.clear();
		
		for (int k : values.keySet()) {
			switch (k) {
			case CloudSimTags.EXPERIMENT:
					if ((Double) values.get(k).get(0) == 20) { // create after initialization at simulation time zero.
						events.add(new Event(chooseDataCenter(), 
								CloudSimTags.VM_CREATE_ACK, createVM()));
					}
				break;

			case CloudSimTags.VM_CREATE_ACK:
					for (Object v : values.get(k)) {
						VMAck va = (VMAck) v;
						if (va.getSuccess() == CloudSimTags.TRUE) {
							Log.printConcatLine("[SUCCESS] VM successfully created.",
									" VM id: ", va.getVmId(), 
									" Datacenter id: ", va.getDatacenterId());
						} else {
							Log.printConcatLine("[FAILED] during VM creation.",
									" VM id: ", va.getVmId(), 
									" Datacenter id: ", va.getDatacenterId());
						}
					}
				
			default:
				break;
			}
		}
		
		return null;
	}
	
	private int chooseDataCenter() {
		return mybroker.getDcs().get(0);
	}

	private Vm createVM() {
		// VM description
		int vmid = 0;
		int mips = 1000;
		long size = 10000; // image size (MB)
		int ram = 512; // vm memory (MB)
		long bw = 1000;
		int pesNumber = 1; // number of cpus
		String vmm = "Xen"; // VMM name

		// create VM
		return new Vm(vmid, mybroker.getId(), mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
	}



}
