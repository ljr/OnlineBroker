package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;

public abstract class Monitor {
	
	List<Double> current;
	
	public List<Double> getCurrent() {
		return current;
	}

	public Monitor() {
		current = new ArrayList<Double>();
	}
	
	protected void sample() {
		current.clear();
		current.add(CloudSim.clock());
	}

	public List<Double> get() {
		sample();
		return getCurrent();
	}
	
}
