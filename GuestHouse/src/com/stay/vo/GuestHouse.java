package com.stay.vo;

import java.util.ArrayList;

public class GuestHouse {
	String id;
	String name;
	String address;
	
	ArrayList<Room> rooms = new ArrayList<>();

	public GuestHouse() {}

	public GuestHouse(String id, String name, String address) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
	}

	public GuestHouse(String id, String name, String address, ArrayList<Room> rooms) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.rooms = rooms;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}
	
	@Override
	public String toString() {
		return "GuestHouse [사업자 등록번호 : " + id + ",가게 이름 : " + name + ", 가게 주소 : " + address + ", 방들 : " + rooms + "]";
	}

	public String excludeRoom() {
		return "GuestHouse [id=" + id + ", name=" + name + ", address=" + address + "]";
	}

}
