package br.usp.icmc.lasdpc.cloudsim;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class Monitor {
	
	protected Map<Integer, Map<String, List<Object>>> values;

	public Monitor() {
		values = new HashMap<Integer, Map<String, List<Object>>>();
	}
	
	public abstract void get();

	
	public Map<Integer, Map<String, List<Object>>> getValues() {
		return values;
	}

	
	public void clearValues(){
		values.clear();
	}

	public void add(int tag, String name, Object value) {
		if (values.get(tag) == null) {
			values.put(tag, new HashMap<String, List<Object>>());
		}
		
		if (values.get(tag).get(name) == null) {
			values.get(tag).put(name, new LinkedList<Object>());
		}

		values.get(tag).get(name).add(value);
	}
}
