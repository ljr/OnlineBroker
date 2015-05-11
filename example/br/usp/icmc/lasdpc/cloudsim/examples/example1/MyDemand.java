package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;

import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.Event;

public class MyDemand extends Demand {

	@Override
	public List<Event> update(List<Double> values) {
		
		// TODO: always clear events before calling it.
		events.clear();
		
		
		if (values.get(0) == 100) {
			events.add(new Event(10, CloudSimTags.END_OF_SIMULATION));
		}
		
		return events;
	}

}
