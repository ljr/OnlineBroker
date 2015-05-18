package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Monitor {
	
	protected Map<Integer, List<Object>> values;

	public Monitor() {
		values = new HashMap<Integer, List<Object>>();
	}
	
	public abstract void get();

	
	public Map<Integer, List<Object>> getValues() {
		return values;
	}

	
	public void clearValues(){
		values.clear();
	}

	public void add(int key, Object value) {
		if (values.get(key) == null) {
			values.put(key, new ArrayList<Object>());
		}
		
		values.get(key).add(value);
	}
}
