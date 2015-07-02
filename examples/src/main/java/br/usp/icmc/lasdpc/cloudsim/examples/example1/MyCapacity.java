package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class MyCapacity extends Capacity {

	
	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {

		// TODO: always clear events before calling it.
		events.clear();
				
		for (int k : values.keySet()) {
			switch (k) {
			case CloudSimTags.EXPERIMENT:
				setCapacity((double) values.get(MonitorValues.CLOCK).get(0));
				break;

			case CloudSimTags.VM_CREATE_ACK:
			case CloudSimTags.VM_DESTROY_ACK:
				processVmAck(values, k);
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
			destroyAllVMs();
		}
	}

	private void processVmAck(Map<Integer, List<Object>> values, int tag) {
		Log.printConcatLine("[MyCapacity]: processing ACK - VM CREATED");
	}

	private void destroyAllVMs() {
		for (Entry<Integer, Vm> e : getMyBroker().getMonitor().getVmList().getVms().entrySet()) {
			events.add(new Event(e.getValue().getHost().getDatacenter().getId(),
					CloudSimTags.VM_DESTROY_ACK, e.getValue()));
		}
	}

	private void createVM() {
		Event e = new Event();
		e.setTag(CloudSimTags.VM_CREATE_ACK);
		e.setData(newVm());
		events.add(e);
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
		Vm vm = new Vm(vmid, mybroker.getId(), mips, pesNumber, ram, bw, size, 
				vmm, new CloudletSchedulerTimeShared());
		return vm;
	}

}
