package br.usp.icmc.lasdpc.cloudsim.examples.example1;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Monitor;

public class MyMonitor extends Monitor {

	@Override
	public void get() {
		add(CloudSimTags.EXPERIMENT, "CLOCK", CloudSim.clock());
	}



}
