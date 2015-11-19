package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class PICapacity extends Capacity {

	private int vmId;
	private int userId;
	private int vmsAtStart;
	
	public PICapacity(int vmsAtStart) {
		this.vmId = 1;
		this.userId = 1;
		this.vmsAtStart = vmsAtStart;
	}
	
	private synchronized int nextId() {
		return this.vmId++;
	}
	
	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		events.clear();
		
		if (CloudSim.clock() == 0) {
			for (int i = 0; i < vmsAtStart; i++) {
				events.add(new Event(/*delay = */ 0, CloudSimTags.VM_CREATE_ACK, 
					newVm()));
			}
		}
		
		double u = (double) values.get(Tags.UTILIZATION).get(0);
		
		if (u > .8) {
			events.add(new Event(/*delay = */ 0, CloudSimTags.VM_CREATE_ACK, 
					new Vm(nextId(), userId, 1000, 1, 4096, 10000, 1024, "XEN", 
							new CloudletSchedulerSpaceShared())));
		}
		
		if (u < .6) {
			events.add(new Event(0, Tags.BLEED, 1));
		}
		
		
		return events;
	}

	private Object newVm() {
		return new Vm(nextId(), userId, 1000, 1, 4096, 10000, 1024, "XEN", 
				new CloudletSchedulerSpaceShared());
	}

}
