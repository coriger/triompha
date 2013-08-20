package com.triompha.dao.test;

public class UidStatPared {

	private long uid;
	private int stat;
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}
	@Override
	public String toString() {
		return "UidStatPared [stat=" + stat + ", uid=" + uid + "]";
	}

}
