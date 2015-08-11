package br.usp.icmc.lasdpc.cloudsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;

import br.usp.icmc.lasdpc.cloudsim.aux.CloudletManager;
import br.usp.icmc.lasdpc.cloudsim.aux.VmManager;


public abstract class Monitor {
	
	protected Map<Integer, List<Object>> values;
	protected VmManager vmManager;
	protected CloudletManager cloudletManager;

	public Monitor() {
		values = new HashMap<Integer, List<Object>>();
		vmManager = new VmManager();
		cloudletManager = new CloudletManager();
	}
	
	public abstract void get();

	
	public Map<Integer, List<Object>> getValues() {
		return values;
	}

	
	public void clearValues() {
		values.clear();
	}

	public void add(int tag, Object value) {
		if (!values.containsKey(tag)) {
			values.put(tag, new ArrayList<Object>());
		}
		
		values.get(tag).add(value);
	}
	
	public List<Object> getValueFromTag(int tag) {
		return values.get(tag);
	}

	public VmManager getVmManager() {
		return vmManager;
	}

	public void setVmManager(VmManager vmList) {
		this.vmManager = vmList;
	}

	public CloudletManager getCloudletManager() {
		return cloudletManager;
	}

	
	public void setCloudletManager(CloudletManager cloudletManager) {
		this.cloudletManager = cloudletManager;
	}
	
	public Collection<Cloudlet> getCloudletList() {
		return cloudletManager.getCloudlets();
	}
	
	public boolean submitCloudlets() {
		return cloudletManager.isThereCloudletsToBeSubmitted();
	}
	
	public boolean allCloudletsProcessed() {
		return cloudletManager.allCloudletsProcessed();
	}
	
}
