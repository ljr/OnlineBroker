package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.distributions.ExponentialDistr;

import br.usp.icmc.lasdpc.cloudsim.Demand;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;
import br.usp.icmc.lasdpc.cloudsim.distributions.PoissonDistr;

public class StepDemand extends Demand {
	// what is the best way of specifying this ID?
	private static final int STEP = 23498;

	private long seed;
	private StepWorkloadBean workload;
	private ExponentialDistr arrival;
	private PoissonDistr service;
	
	private int cloudletId;
	
	
	public StepDemand(long seed, StepWorkloadBean workload) {
		this.seed = seed;
		this.workload = workload;
		setWorkload(workload.getLambdaBefore(), workload.getMuBefore());
		this.cloudletId = 1;
	}
	
	private synchronized int nextId() {
		return this.cloudletId++;
	}
	
	
	public void setWorkload(double lambda, long mu) {
		arrival = new ExponentialDistr(seed, lambda);
		service = new PoissonDistr(seed, mu);
	}
	
	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		cloudlets.clear();
		
		if (CloudSim.clock() == 0) {
			// TODO: is this suggesting a new name for cloudlets?
			cloudlets.add(new Event(workload.getChangeTime(), STEP));
		} else if (CloudSim.clock() == workload.getChangeTime()) { 
			setWorkload(workload.getLambdaAfter(), workload.getMuAfter());
		}
		
		cloudlets.add(new Event(arrival.sample(), CloudSimTags.CLOUDLET_SUBMIT, newRequest()));
		
		return cloudlets;
	}
	
	private Cloudlet newRequest() {
		return new Cloudlet(nextId(), service.sample(), 1, 0, 1024, 
				new UtilizationModelFull(), new UtilizationModelFull(), 
				new UtilizationModelFull());
	}

	public static StepWorkloadBean newWorkload(double lambdaBefore, 
			double lambdaAfter, long muBefore, long muAfter, 
			double changeTime) {
		return new StepWorkloadBean(lambdaBefore, lambdaAfter, muBefore, 
				muAfter, changeTime);
	}
}

class StepWorkloadBean {
	private double lambdaBefore;
	private double lambdaAfter;
	private long muBefore;
	private long muAfter;
	private double changeTime;
	
	public StepWorkloadBean(double lambdaBefore, double lambdaAfter, 
			long muBefore, long muAfter, double changeTime) {
		this.lambdaBefore = lambdaBefore;
		this.lambdaAfter = lambdaAfter;
		this.muBefore = muBefore;
		this.muAfter = muAfter;
		this.changeTime = changeTime;
	}
	
	public double getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(double changeTime) {
		this.changeTime = changeTime;
	}

	public double getLambdaBefore() {
		return lambdaBefore;
	}
	public void setLambdaBefore(double lambdaBefore) {
		this.lambdaBefore = lambdaBefore;
	}
	public double getLambdaAfter() {
		return lambdaAfter;
	}
	public void setLambdaAfter(double lambdaAfter) {
		this.lambdaAfter = lambdaAfter;
	}
	public long getMuBefore() {
		return muBefore;
	}
	public void setMuBefore(long muBefore) {
		this.muBefore = muBefore;
	}
	public long getMuAfter() {
		return muAfter;
	}
	public void setMuAfter(long muAfter) {
		this.muAfter = muAfter;
	}
	
}
