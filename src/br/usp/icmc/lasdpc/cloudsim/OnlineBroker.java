package br.usp.icmc.lasdpc.cloudsim;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;


public class OnlineBroker extends SimEntity {
	/**
	 * List of all datacenters instantiated in simulation. @see getDcs
	 */
	protected List<Integer> dcs;

	/**
	 * Map datacenter to its characteristics. @see getCharacteristics  
	 */
	protected Map<Integer, DatacenterCharacteristics> characteristics;
	
	/** 
	 * Sample time to monitoring, processing and afterwards effecting. 
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
		
		characteristics = new HashMap<Integer, DatacenterCharacteristics>();
	}
	

	@Override
	public void startEntity() {
		sendNow(getId(), SAMPLE_TIME);
		sendNow(getId(), CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST);
		Log.printConcatLine(getName(), " it started.");		
	}
	

	@Override
	public void processEvent(SimEvent ev) {
		switch (ev.getTag()) {
		case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
				requestDCCharacteristics(ev);
			break;
			
		case CloudSimTags.RESOURCE_CHARACTERISTICS:
				getCharacteristic(ev);
			break;
			
		case SAMPLE_TIME:
				processSample(ev);
			break;
			
		case CloudSimTags.VM_CREATE_ACK:
				processVmCreate(ev);
			break;
			
		case CloudSimTags.CLOUDLET_RETURN:
				processCloudletReturn(ev);
			break;
		
		case CloudSimTags.END_OF_SIMULATION:
				shutdownEntity();
			break;
			
		default:
				Log.print("Nothing to do here...");
			break;
		}
	}

	
	private void processCloudletReturn(SimEvent ev) {
		
	}


	private void processVmCreate(SimEvent ev) {
		int[] data = (int[]) ev.getData();
		monitor.add(MonitorTypes.VMACK, new VMAck(data[0], data[1], data[2]));
	}


	protected void getCharacteristic(SimEvent ev) {
		DatacenterCharacteristics data = (DatacenterCharacteristics) ev.getData();
		characteristics.put(data.getId(), data);
	}

	
	protected void requestDCCharacteristics(SimEvent ev) {
		dcs = CloudSim.getCloudResourceList();
		
		for (Integer dc : dcs) {
			sendNow(dc, CloudSimTags.RESOURCE_CHARACTERISTICS, getId());
		}
	}

	
	@Override
	public void shutdownEntity() {
		Log.printConcatLine("Ending ", getName(), " at CloudSim.Clock(): ", CloudSim.clock());
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
		int id;
		for (Event e : events) {
			id = e.getDest() == -1 ? getId() : e.getDest();
			send(id, e.getDelay(), e.getTag(), e.getData());
		}
	}



	//=========================================================================
	// Gets available for monitoring 
	//=========================================================================
	
	public List<Integer> getDcs() {
		return dcs;
	}

	
	public Map<Integer, DatacenterCharacteristics> getCharacteristics() {
		return characteristics;
	}

	
}
