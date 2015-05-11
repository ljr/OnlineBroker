package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

public class OnlineBroker extends DatacenterBroker {
	
	/** 
	 * Sample time to monitoring, processing and afterwards effecting 
	 */
	private int sampleTime;
	
	/** 
	 * Unique identification (tag) for Sample events. 
	 */
	private static final int SAMPLE_TIME = 80801;
	
	/** 
	 * Monitor class, its responsibility is get information about the system 
	 */
	private Monitor monitor;
	
	/** 
	 * Effector class, its responsibility is set demand and capacity 
	 * against/of the systems 
	 */
	private Effector effector;
	
	/** 
	 * Capacity class, its responsibility is generate a list of events about 
	 * the amount of VMs in the system  
	 */
	private Capacity capacity;
	
	/** 
	 * Demand class, its responsibility is generate a list of events about 
	 * the tasks to be imposed in the system 
	 */
	private Demand demand;
	
	
	public OnlineBroker(String name, int sampleTime, Monitor monitor, 
			Effector effector, Capacity capacity, Demand demand) throws Exception {
		super(name);
		
		this.sampleTime = sampleTime;
		this.monitor = monitor;
		this.capacity = capacity;
		this.effector = effector;
		this.effector.setMybroker(this);
		this.demand = demand;
	}

	@Override
	public void startEntity() {
		super.startEntity();
		sendNow(getId(), SAMPLE_TIME);
	}

	@Override
	protected void processOtherEvent(SimEvent ev) {
		switch (ev.getTag()) {
		case SAMPLE_TIME:
				processSample(ev);
			break;
			
		default:
				Log.print("Nothing to do here...");
			break;
		}
	}

	@Override
	protected void processResourceCharacteristics(SimEvent ev) {
		DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
		getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

		if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) {
			setDatacenterRequestedIdsList(new ArrayList<Integer>());
		}
	}

	@Override
	public void shutdownEntity() {
		Log.printConcatLine("Ending " + getName() + " at CloudSim.Clock(): " 
				+ CloudSim.clock());
		// TODO: will it always work?
		setState(FINISHED);
	}
	
	private void processSample(SimEvent ev) {
		effector.set(
				capacity.update(monitor.get()), 
				demand.update(monitor.get())
		);
		
		send(getId(), sampleTime, SAMPLE_TIME);
	}
	
	public void sendEvents(List<Event> events) {
		for (Event e : events) {
			send(getId(), e.getDelay(), e.getTag(), e.getData());
		}
	}
}
