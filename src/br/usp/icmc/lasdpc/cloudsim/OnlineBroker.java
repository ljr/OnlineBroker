package br.usp.icmc.lasdpc.cloudsim;

import java.util.List;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;



public class OnlineBroker extends DatacenterBroker {

	/** Sample time to monitoring, processing and afterwards effecting */
	private int sampleTime;
	
	/** Unique identification (tag) for Sample events. */
	private static final int SAMPLE_TIME = 80801;
	
	/** Monitor class, its responsibility is get information about the system */
	private Monitor monitor;
	
	/** Effector class, its responsibility is set demand and capacity against/of the systems */
	private Effector effector;
	
	/** Capacity class, its responsibility is generate a list of events about the amount of VMs in the system  */
	private Capacity capacity;
	
	/** Demand class, its responsibility is generate a list of events about the tasks to be imposed in the system */
	private Demand demand;
	
	
	public OnlineBroker(String name, int sampleTime, Monitor monitor, 
			Effector effector, Capacity capacity, Demand demand) throws Exception {
		super(name);
		
		this.sampleTime = sampleTime;
		this.monitor = monitor;
		this.capacity = capacity;
		this.effector = effector;
		//this.effector.setMybroker(this);
		this.demand = demand;
	}

	@Override
	public void startEntity() {
		sendNow(getId(), SAMPLE_TIME);
	}
	
	public void done() {
		Log.printConcatLine("Ending " + getName() + " at CloudSim.Clock(): " 
				+ CloudSim.clock());
		setState(FINISHED);
	}
	
	
	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		case SAMPLE_TIME:
				processSample(ev);
			break;
			
		case CloudSimTags.END_OF_SIMULATION:
				done();
			break;

		default:
				super.processEvent(ev);
			break;
		}
	}

	private void processSample(SimEvent ev) {
		// process...
		List<Double> values = monitor.get();
		System.out.println(values.get(0) + " Sample time is ticking...");
		effector.set(capacity.update(values), demand.update(values));
		
		// schedule the next sample time event.
		send(getId(), sampleTime, SAMPLE_TIME);
	}
	
	public void sendEvents(List<Event> events) {
		for (Event e : events) {
			send(getId(), e.getDelay(), e.getTag(), e.getData());
		}
	}

}
