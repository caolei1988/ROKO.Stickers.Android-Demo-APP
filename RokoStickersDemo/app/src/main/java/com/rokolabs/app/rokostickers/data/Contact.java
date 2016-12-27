package com.rokolabs.app.rokostickers.data;

import java.io.Serializable;

public class Contact implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String phone;

	public Contact(String n, String p) {
		this.name = n;
		this.phone = p;
	}

}
