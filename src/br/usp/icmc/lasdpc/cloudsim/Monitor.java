package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;


public abstract class Monitor {
	
	protected Map<Integer, List<Object>> values;
	
	public Map<Integer, List<Object>> getValues() {
		return values;
	}

	public boolean isNull(int key) {
		return values.get(key) == null;
	}
	
	public void add(int key, Object value) {
		if (isNull(key)) {
			values.put(key, new ArrayList<Object>());
		}
		
		values.get(key).add(value);
	}

	public Monitor() {
		values = new HashMap<Integer, List<Object>>();
	}
	
	protected void sample() {
		add(CloudSimTags.EXPERIMENT, CloudSim.clock());
	}

	public Map<Integer, List<Object>> get() {
		sample();
		return values;
	}
	
	public void clear() {
		values.clear();
	}

}
