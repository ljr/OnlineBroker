package br.usp.icmc.lasdpc.cloudsim.aux;

import org.cloudbus.cloudsim.core.CloudSimTags;

public class Ack {

	private int datacenterId;
	private int id;
	private int success;
	private String desc;

	
	public Ack(int datacenterId, int id, int success, String desc) {
		this.datacenterId = datacenterId;
		this.id = id;
		this.success = success;
		this.desc = desc;
	}
	
	public Ack(int datacenterId, int id, int success) {
		this(datacenterId, id, success, "");
	}
	
	public Ack(int []data) {
		// data[0]: datacenter.id
		// data[1]: <instance>.id (Vm, Cloudlet etc.)
		// data[2]: success?
		this(data[0], data[1], data[2]);
	}
	
	public int getDatacenterId() {
		return datacenterId;
	}

	
	public void setDatacenterId(int datacenterId) {
		this.datacenterId = datacenterId;
	}
	

	public int getId() {
		return id;
	}
	

	public void setId(int id) {
		this.id = id;
	}

	
	public int getSuccess() {
		return success;
	}

	public boolean succeed() {
		return getSuccess() == CloudSimTags.TRUE;
	}
	
	public void setSuccess(int success) {
		this.success = success;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "[" + desc + "_ACK] {datacenterId: " + datacenterId + ", entityId: " + id + 
				", success: " + success + "}";
	}
	
}
