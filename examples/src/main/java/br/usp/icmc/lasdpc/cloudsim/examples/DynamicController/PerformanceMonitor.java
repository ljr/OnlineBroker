package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import br.usp.icmc.lasdpc.cloudsim.Monitor;


public class PerformanceMonitor extends Monitor {
	private double changeTime;
	private RealisticVmManager rvm;

	public double getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(double changeTime) {
		this.changeTime = changeTime;
	}

	public RealisticVmManager vmManager() {
		return this.rvm;
	}
	
	public PerformanceMonitor() {
		this.rvm = new RealisticVmManager();
	}

	public boolean canReceiveCloudlets() {
		return vmManager().getRunning().size() > 0;
	}
	
	public int vmsInSystem() {
		return vmManager().getRunning().size();
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
		
		return u / vmManager().getRunning().size();
	}
}
