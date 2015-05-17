package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import java.util.List;
import java.util.Map;

import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.Event;

public class MyDemand extends Demand {

	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		
		// TODO: always clear events before calling it.
		events.clear();
		


		return events;
	}

	
}
