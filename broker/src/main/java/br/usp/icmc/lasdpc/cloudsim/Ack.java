package br.usp.icmc.lasdpc.cloudsim;

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
