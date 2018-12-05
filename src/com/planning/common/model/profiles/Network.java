package com.planning.common.model.profiles;

import java.util.LinkedHashMap;
import java.util.Map;
import com.planning.common.model.Entity;

/**
 * This entity holds the traversal path for each part.
 * @author Karthik
 *
 */
public class Network implements Entity {

	public Network() {
		super();
		this.componentFlows = new LinkedHashMap<>();
	}

	@Override
	public String getId() {
		return part;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public void addComponentFlow(String part, String bomNumber, Double productionFactor) {
		ComponentFlow altComponentFlow = componentFlows.get(bomNumber);
		
		if (altComponentFlow == null) {
			altComponentFlow = new ComponentFlow();
			componentFlows.put(bomNumber, altComponentFlow);
		}
		altComponentFlow.setBomNumber(bomNumber);
		altComponentFlow.addComponent(part, productionFactor);
	}

	public Map<String, ComponentFlow> getComponentFlows() {
		return componentFlows;
	}
	
	public ComponentFlow getComponentFlow(String bomNumber) {
		return componentFlows.get(bomNumber);
	}
	

	private String part;
	private final Map<String, ComponentFlow> componentFlows;
}
