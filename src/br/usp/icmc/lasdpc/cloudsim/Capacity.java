package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Capacity {
	
	protected List<Integer> vmIds;
	
	public Capacity() {
		vmIds = new ArrayList<Integer>();
	}
	
	public abstract List<Event> update(Map<Integer, List<Object>> values);

}
