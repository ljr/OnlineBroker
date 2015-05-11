package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.core.CloudSim;


public abstract class Monitor {
	
	protected Map<MonitorTypes, List<Object>> values;
	
	public Map<MonitorTypes, List<Object>> getValues() {
		return values;
	}

	public boolean isNull(MonitorTypes key) {
		return values.get(key) == null;
	}
	
	public void add(MonitorTypes key, Object value) {
		if (isNull(key)) {
			values.put(key, new ArrayList<Object>());
		}
		
		values.get(key).add(value);
	}

	public Monitor() {
		values = new HashMap<MonitorTypes, List<Object>>();
	}
	
	protected void sample() {
		add(MonitorTypes.DOUBLE, CloudSim.clock());
	}

	public Map<MonitorTypes, List<Object>> get() {
		sample();
		return values;
	}

}
