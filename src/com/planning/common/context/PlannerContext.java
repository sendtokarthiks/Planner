package com.planning.common.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import com.planning.common.model.input.Demand;
import com.planning.common.model.input.Supply;
import com.planning.common.model.output.DemandPlan;
import com.planning.common.model.profiles.Network;
import com.planning.common.model.profiles.PlanPath;
/**
 * This Class holds the necessary entities for planning.
 * @author Karthik
 *
 */
@Component
public class PlannerContext {
	
	public PlannerContext() {
		super();
		this.networks = new HashMap<>();
		this.inventoryProfile = new ConcurrentHashMap<>();
		this.demandPlansMap = new LinkedHashMap<>();
		this.planPaths = new ConcurrentHashMap<>();
		this.demands = new ArrayList<>();
	}
	
	public void addSupply(Supply supply) {
		List<Supply> supplies = inventoryProfile.get(supply.getPart());
		if (supplies == null) {
			supplies = new ArrayList<>();
			inventoryProfile.put(supply.getPart(), supplies);
		}
		supplies.add(supply);
	}
	
	public void addDemand(Demand demand) {
		demands.add(demand);
	}

	public List<Demand> getDemands() {
		return demands;
	}
	
	public List<Supply> getInventoryProfile(String part) {
		//TODO : Extend to get supply for a given date, hence created as list.
		return inventoryProfile.get(part);
	}
	
	public Map<String, Network> getNetworks() {
		return networks;
	}
	
	public Network getNetwork(String part) {
		return networks.get(part);
	}

	public void addNetwork(Network network) {
		networks.put(network.getPart(), network);
	}

	public Map<String, List<Supply>> getInventoryProfile() {
		return inventoryProfile;
	}

	public Map<String, List<DemandPlan>> getDemandPlansMap() {
		return demandPlansMap;
	}
	
	public void addDemandPlan(DemandPlan demandPlan) {
		List<DemandPlan> demandPlans = demandPlansMap.getOrDefault(demandPlan.getOrderNumber(), new ArrayList<>());
		demandPlans.add(demandPlan);
		demandPlansMap.put(demandPlan.getOrderNumber(), demandPlans);
	}

	public Map<String, Map<String, PlanPath>> getPlanPaths() {
		return planPaths;
	}
	
	public Map<String, PlanPath> getPlanPath(String bomNumber) {
		return planPaths.get(bomNumber);
	}
	
	public void addPlanPath(String bomNumber, PlanPath planPath) {
		Map<String, PlanPath> levelPlanPath = planPaths.getOrDefault(bomNumber, new HashMap<>());
		levelPlanPath.put(planPath.getPart(), planPath);
		planPaths.put(bomNumber, levelPlanPath);
	}

	private final Map<String, Network> networks;
	private final Map<String, List<Supply>> inventoryProfile;
	private final Map<String, List<DemandPlan>> demandPlansMap;
	private final Map<String, Map<String, PlanPath>> planPaths;
	private final List<Demand> demands;
}
