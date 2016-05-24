package br.usp.icmc.lasdpc.cloudsim.aux;

import org.cloudbus.cloudsim.Vm;

public class MetaVm {
	private int id;
	private Vm vm;
	private double deadline;
	
	MetaVm(double deadline, Vm vm) throws Exception {
		this.deadline = deadline;
		
		if (vm == null) {
			throw new Exception("Vm cannot be null.");
		}
		
		this.vm = vm;
		this.id = vm.getId();
	}
	
	public Vm getVm() {
		return vm;
	}
	
	public double getDeadline() {
		return deadline;
	}
	
	public int getId() {
		return id;
	}
}