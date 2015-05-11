package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Demand {
	
	protected List<Event> events;
	
	public Demand() {
		events = new ArrayList<Event>();
	}
	
	public abstract List<Event> update(Map<MonitorTypes, List<Object>> values);

}
