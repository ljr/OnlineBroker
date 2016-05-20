package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import org.cloudbus.cloudsim.Vm;

public class MetaVm {
	private int id;
	private Vm vm;
	private double deadline;
	
	MetaVm(double deadline, Vm vm) {
		this.deadline = deadline;
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