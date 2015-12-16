package org.ku8eye.bean.deploy;

/**
 * install param
 * 
 * @author wuzhih
 *
 */
public class InstallParam implements Cloneable {
	private String name;
	private String value;
	private String describe;

	public InstallParam()
	{
		
	}
	
	public InstallParam(String name,String value,String describe)
	{
		this.name=name;
		this.value=value;
		this.describe=describe;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	@Override
	public InstallParam clone() {
		try {
			return (InstallParam) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
