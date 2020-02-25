package com.example.integrationtestspringkafka;

public class ExampleDTO {

	private String name;
	private String description;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "ExampleDTO [name=" + name + ", description=" + description + "]";
	}
	
}
