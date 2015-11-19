package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Vm;

import br.usp.icmc.lasdpc.cloudsim.aux.Event;


public abstract class Effector {
	
	protected OnlineBroker mybroker;
	protected List<Event> cap;
	protected List<Event> dem;

	public Effector() {
		cap = new ArrayList<Event>();
		dem = new ArrayList<Event>();
	}
	
	public abstract void set(List<Event> cap, List<Event> dem);
	
	public void clear() {
		cap.clear();
		dem.clear();
	}
	
	public void update(List<Event> cap, List<Event> dem) {
		clear();
		
		set(cap, dem);
		
		for (Event e : cap) {
			Vm vm = (Vm) e.getData();
			mybroker.getMonitor().getVmManager().getVms().put(vm.getId(), vm);
		}
		
		for (Event e: dem) {
			Cloudlet cl = (Cloudlet) e.getData();
			mybroker.getMonitor().getCloudletManager().submit(cl);;
		}
	}
	
	public OnlineBroker getMybroker() {
		return mybroker;
	}

	public void setMybroker(OnlineBroker mybroker) {
		this.mybroker = mybroker;
	}
	
}
