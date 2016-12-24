package japp.test;

import java.util.List;

import japp.model.entity.Entity;

public class EntityA extends Entity {
	
	private static final long serialVersionUID = -8131445218900736897L;
	
	private Integer a;
	private String b;
	private List<EntityB> entitiesB;
	
	public Integer getA() {
		return a;
	}
	
	public void setA(Integer a) {
		this.a = a;
	}
	
	public String getB() {
		return b;
	}
	
	public void setB(String b) {
		this.b = b;
	}
	
	public List<EntityB> getEntitiesB() {
		return entitiesB;
	}
	
	public void setEntitiesB(List<EntityB> entitiesB) {
		this.entitiesB = entitiesB;
	}
}
