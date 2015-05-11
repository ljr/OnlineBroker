package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.Event;
import br.usp.icmc.lasdpc.cloudsim.MonitorTypes;

public class MyDemand extends Demand {

	@Override
	public List<Event> update(Map<MonitorTypes, List<Object>> values) {
		
		// TODO: always clear events before calling it.
		events.clear();
		
		for (MonitorTypes k : values.keySet()) {
			switch (k) {
			case DOUBLE:
				if ((Double) values.get(k).get(0) == 100) {
					events.add(new Event(10, CloudSimTags.END_OF_SIMULATION));
					System.out.println("end"); System.exit(0);
				}
				break;

			default:
				break;
			}
		}
		/*
		if ((int) values.get(0) == 100) {
			events.add(new Event(10, CloudSimTags.END_OF_SIMULATION));
		}
		*/
		return events;
	}

	
}
