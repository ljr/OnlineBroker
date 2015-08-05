package br.usp.icmc.lasdpc.cloudsim.examples.DCBroker;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class BatchCapacity extends Capacity {

	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		double clock = (Double) values.get(CloudSimTags.EXPERIMENT).get(0); 

		if (clock == 0) {
			double delay = 0;
			int tag = CloudSimTags.VM_CREATE_ACK;
			for (Vm vm : mybroker.getMonitor().getVmList().getVms().values()) {
				events.add(new Event(delay, tag, vm));
			}	
		}
		
		return events;
	}

}
