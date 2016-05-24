package br.usp.icmc.lasdpc.cloudsim.examples.DynamicController;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;

import br.usp.icmc.lasdpc.cloudsim.Capacity;
import br.usp.icmc.lasdpc.cloudsim.OnlineBroker;
import br.usp.icmc.lasdpc.cloudsim.aux.Ack;
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
		this.userId = -1; // MUST be set as mybroker.getId() @see setMyBroker
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
		this.mon = (PerformanceMonitor) mybroker.getMonitor();
		this.userId = mybroker.getId();
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
		} else if (mon.canReceiveCloudlets()) {
			// TODO: is howManyVmsInSystem returning the right value?
			howManyVms = controller(error) - mon.vmsInSystem();
		} else {
			howManyVms = 0;
		}
				
		
		if (howManyVms > 0) {
			for (int i = 0; i < howManyVms; i++) {
				events.add(new Event(/*delay = */ 0, CloudSimTags.VM_CREATE_ACK, 
						newVm()));
			}
		} else if (howManyVms < 0) {
			events.add(new Event(/*delay = */ 0, Tags.DESTROY, -howManyVms));
		}
		
		processAck(values, CloudSimTags.VM_CREATE_ACK);
		
		return events;
	}
	
	private void processAck(Map<Integer, List<Object>> values, int tag) {
		
		List<Object> acks = values.get(tag);
		if (acks == null) {
			return;
		}
		
		
		for (Object ack : acks) {
			Ack vmAck = (Ack) ack;
			if (tag == CloudSimTags.VM_CREATE_ACK) {
				mon.getVmManager().bootingToRunning(vmAck);
//				Log.printLine(">>>> VM_CREATE_ACK received running: " + mon.getVmManager().getRunning().size());
			}
				
			//Log.printLine(vmAck);
		}
	}

	private void finishExecution() {
		double delay = 0;
		Log.printLine(">>> Ending simulation.");
		mybroker.sendEvent(new Event(delay, CloudSimTags.END_OF_SIMULATION));
	}
	
	private long controller(double error) {
		double diff = kd * (error - lastError);
		double prop = kp * error;
		integral += ki * error;
		lastError = error;
		return Math.round(Math.floor(prop + integral + diff));
	}
	
	private Integer newVm() {
		Integer vmId = -1;

		try {
			vmId = (mon.getVmManager().newVm(nextId(), userId, 1000, 1, 4096, 
					10000, 1024, "XEN", new CloudletSchedulerSpaceShared()))
					.getId();
		} catch (Exception e) {
			Log.printLine(e.getMessage());
			finishExecution();
		}
		
//		Log.printLine("vm.getId(): " + vmId);
		return vmId;
	}

}
