package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import br.usp.icmc.lasdpc.cloudsim.Monitor;
import br.usp.icmc.lasdpc.cloudsim.aux.VmManager;


public class PerformanceMonitor extends Monitor {
	private double changeTime;
	private VmManager rvm;

	public double getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(double changeTime) {
		this.changeTime = changeTime;
	}

	public boolean canReceiveCloudlets() {
		return getVmManager().getRunning().size() > 0;
	}
	
	public int vmsInSystem() {
		return getVmManager().getRunning().size();
	}
	
	@Override
	public void get() {
		add(Tags.UTILIZATION, getUtilization());
	}

	
	private double getUtilization() {
		double u = 0;
		
		for (Vm vm : rvm.getRunning().values()) {
			u += vm.getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
		}
		
		return u / getVmManager().getRunning().size();
	}
}
