package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;
import br.usp.icmc.lasdpc.cloudsim.aux.Event;

public class PICapacity extends Capacity {

	private int vmId;
	private int userId;
	private int vmsAtStart;
	private double setPoint;
	private double kp;
	private double ki;
	private double integral;
	private double kd;
	private double lastError;
	private PerformanceMonitor mon;
	
	public PICapacity(int vmsAtStart, double setPoint, double kp, double ki, 
			double kd) {
		this.vmId = 1;
		this.userId = 1;
		this.vmsAtStart = vmsAtStart;
		this.setPoint = setPoint;
		this.kp = kp;
		this.ki = ki;
		this.integral = 0;
		this.kd = kd;
		this.lastError = 0;
	}
	
	@Override
	public void setMyBroker(OnlineBroker mybroker) {
		mon = (PerformanceMonitor) mybroker.getMonitor();
	}
	
	private synchronized int nextId() {
		return this.vmId++;
	}
	
	@Override
	public List<Event> update(Map<Integer, List<Object>> values) {
		events.clear();
	
		double error = setPoint - (double) values.get(Tags.UTILIZATION).get(0);
		long howManyVms;
		
		if (CloudSim.clock() == 0) {
			howManyVms = vmsAtStart;
		} else {
			// TODO: is howManyVmsInSystem returning the right value?
			howManyVms = controller(error) - mon.howManyVmsInSystem();
		}
				
		
		if (howManyVms > 0) {
			for (int i = 0; i < howManyVms; i++) {
				events.add(new Event(/*delay = */ 0, CloudSimTags.VM_CREATE_ACK, 
						newVm()));
			}
		} else if (howManyVms < 0) {
			events.add(new Event(/*delay = */ 0, Tags.BLEED, -howManyVms));
		}
		
		return events;
	}

	private long controller(double error) {
		double diff = kd * (error - lastError);
		double prop = kp * error;
		integral += ki * error;
		lastError = error;
		return Math.round(Math.floor(prop + integral + diff));
	}
	
	private Object newVm() {
		return new Vm(nextId(), userId, 1000, 1, 4096, 10000, 1024, "XEN", 
				new CloudletSchedulerSpaceShared());
	}

}
