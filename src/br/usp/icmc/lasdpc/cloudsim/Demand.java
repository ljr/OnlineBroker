package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;

public abstract class Demand {
	
	protected List<Event> events;
	
	public Demand() {
		events = new ArrayList<Event>();
	}
	
	public abstract List<Event> update(List<Double> values);

}
