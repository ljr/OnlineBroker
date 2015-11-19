package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import br.usp.icmc.lasdpc.cloudsim.Monitor;

public class PerformanceMonitor extends Monitor {


	
	@Override
	public void get() {
		
		double u = 0;
		for (Vm vm : getVmManager().getCreatedList()) {
			u += vm.getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
		}
		u /= getVmManager().getCreated();
		
		add(Tags.UTILIZATION, u);
	}

}
