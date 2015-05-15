package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.Event;

public class MyDemand extends Demand {

	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		
		// TODO: always clear events before calling it.
		events.clear();
		
		for (Integer k : values.keySet()) {
			switch (k) {
			case CloudSimTags.EXPERIMENT:
				if ((Double) values.get(k).get(0) == 100) {
					
					System.out.println("End of simulatin triggered. Cloudsim.clock() = " + (Double) values.get(k).get(0));
					events.add(new Event(10, CloudSimTags.END_OF_SIMULATION));
				}
				break;

			default:
				
				break;
			}
		}

		return events;
	}

	
}
