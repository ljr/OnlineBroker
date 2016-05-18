package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import br.usp.icmc.lasdpc.cloudsim.Monitor;

public class PerformanceMonitor extends Monitor {



	
	private Map<Integer, MetaVm> bleeding;
	private Map<Integer,MetaVm> starting;
	private double changeTime;

	public double getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(double changeTime) {
		this.changeTime = changeTime;
	}

	public PerformanceMonitor() {
		bleeding = new ConcurrentHashMap<Integer,MetaVm>();
		starting = new ConcurrentHashMap<Integer,MetaVm>();
	}

	public Map<Integer, MetaVm> getBleeding() {
		return bleeding;
	}
	
	public Map<Integer, MetaVm> getStarting() {
		return starting;
	}
	
	public boolean canReceiveCloudlets() {
		return getVmManager().getCreatedMap().size() > 0;
	}
	
	public int howManyVmsInSystem() {
		return getVmManager().getCreated() + starting.size() - bleeding.size();
	}
	
	@Override
	public void get() {
		add(Tags.UTILIZATION, getUtilization());
	}

	
	private double getUtilization() {
		double u = 0;
		
		for (Vm vm : getVmManager().getCreatedList()) {
			u += vm.getCloudletScheduler().getTotalUtilizationOfCpu(CloudSim.clock());
		}
		
		return u / getVmManager().getCreated();
	}
}
