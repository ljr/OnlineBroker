package br.usp.icmc.lasdpc.cloudsim.examples.DCBroker;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class BatchCapacity extends Capacity {

	private static final double DELAY = 0;
	private static final int TAG = CloudSimTags.VM_CREATE_ACK;
	
	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		events.clear();
		
		for (Vm vm : mybroker.getMonitor().getVmManager().getSubmitList()) {
			events.add(new Event(DELAY, TAG, vm));
		}	
		
		return events;
	}

}
