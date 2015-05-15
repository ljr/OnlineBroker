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
import br.usp.icmc.lasdpc.cloudsim.VMAck;

public class MyCapacity extends Capacity {

	
	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		
		// TODO: always clear events before calling it.
		events.clear();
				
		for (int k : values.keySet()) {
			switch (k) {
			case CloudSimTags.EXPERIMENT:
					double clock = (double) values.get(k).get(0);
					if (clock == 10) { // create after initialization at simulation time zero.
						Vm vm = createVM();
						create(vm);
						events.add(new Event(chooseDataCenter(), 
								CloudSimTags.VM_CREATE_ACK, vm));
					} else if (clock == 40) {
						for (int vm : vms.keySet()) {
							events.add(new Event(getDatacenter(vm), 
									CloudSimTags.VM_DESTROY, 
									getVm(vm))); // TODO: INSPECT ME!
						}
					}
				break;

			case CloudSimTags.VM_CREATE_ACK:
			case CloudSimTags.VM_DESTROY_ACK:
					for (Object v : values.get(k)) {
						VMAck va = (VMAck) v;
						if (va.getSuccess() == CloudSimTags.TRUE) {
							Log.printConcatLine(CloudSim.clock(), ": [SUCCESS] VM ACK received.",
									" VM id: ", va.getVmId(), 
									" Datacenter id: ", va.getDatacenterId());
							setVMDatacenter(va.getVmId(), 
									k == CloudSimTags.VM_CREATE_ACK
											? va.getDatacenterId()
											: null);
						} else {
							Log.printConcatLine("[FAILED] during VM creation.",
									" VM id: ", va.getVmId(), 
									" Datacenter id: ", va.getDatacenterId());
						}
					}
					
				break;
			
			default:
				break;
			}
		}
		
		return events;
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
