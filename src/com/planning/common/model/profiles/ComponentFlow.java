package com.planning.common.model.profiles;

import java.util.LinkedHashMap;
import java.util.Map;
import com.planning.common.model.Entity;

/**
 * This entity holds all component flows. Components are for storing preferred and non-preferred paths.
 * @author Karthik
 *
 */
public class ComponentFlow implements Entity {

	public ComponentFlow() {
		this.components = new LinkedHashMap<>();
	}

	@Override
	public String getId() {
		return bomNumber;
	}

	public String getBomNumber() {
		return bomNumber;
	}

	public void setBomNumber(String bomNumber) {
		this.bomNumber = bomNumber;
	}

	public Map<String, Double> getComponents() {
		return components;
	}
	
	public void addComponent(String part, Double productionFactor) {
		components.put(part, productionFactor);
	}

	private String bomNumber;
	private final Map<String, Double> components;
}
