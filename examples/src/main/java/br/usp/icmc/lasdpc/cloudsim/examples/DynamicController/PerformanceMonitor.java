package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import br.usp.icmc.lasdpc.cloudsim.Monitor;

public class PerformanceMonitor extends Monitor {

	public class MetaVm {
		private Vm vm;
		private double deadline;
		
		MetaVm(double deadline, Vm vm) {
			this.deadline = deadline;
			this.vm = vm;
		}
		
		public Vm getVm() {
			return vm;
		}
		
		public double getDeadline() {
			return deadline;
		}
	}

	
	private Map<Integer, MetaVm> bleeding;
	private Map<Integer,MetaVm> starting;

	public PerformanceMonitor() {
		bleeding = new HashMap<Integer,MetaVm>();
		starting = new HashMap<Integer,MetaVm>();
	}

	public Map<Integer, MetaVm> getBleeding() {
		return bleeding;
	}
	
	public Map<Integer, MetaVm> getStarting() {
		return starting;
	}
	
	public boolean canReceiveCloudlets() {
		return getVmManager().getCreated() > 0;
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
