package br.usp.icmc.lasdpc.cloudsim.examples.DCBroker;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Monitor;

public class DCMonitor extends Monitor {
	/** TODO: what is the best value for it? */
	public static final int VMS_STATUS = 12345;
	
	public boolean allVmsCreated() {
		return getVmManager().allRequestedVmsDone();
	}
	
	@Override
	public void get() {
		add(CloudSimTags.EXPERIMENT, CloudSim.clock());
		add(VMS_STATUS, allVmsCreated());
	}

}
