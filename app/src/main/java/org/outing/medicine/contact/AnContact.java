package org.outing.medicine.contact;

public class AnContact {
	private String name;
	private String phone;
	private String relative;
	private String icon_path;

	// 构造器
	public AnContact() {
		name = "";
		phone = "";
		relative="";
		icon_path = "";
	}

	public AnContact(String name, String phone,String relative, String icon_path) {
		this.name = name;
		this.phone = phone;
		this.relative=relative;
		this.icon_path = icon_path;
	}

	// getter
	public String getName() {return name;}

	public String getRelative() {return relative;}

	public String getPhone() {return phone;}

	public String getIconPath() {
		return icon_path;
	}

	// setter
	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setRelative(String relative) {this.relative = relative;}

	public void setIconPath(String icon_path) {
		this.icon_path = icon_path;
	}
}
