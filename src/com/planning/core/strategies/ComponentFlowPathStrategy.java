package com.planning.core.strategies;

import java.util.Iterator;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.planning.common.context.PlannerContext;
import com.planning.common.model.profiles.ComponentFlow;
import com.planning.common.model.profiles.Network;
import com.planning.core.service.StrategyExecutor;
/**
 * This class does a depth first strategy to look for components and corresponding stock.
 * @author Karthik.
 *
 */
public class ComponentFlowPathStrategy implements PlannerStrategy {

	public static final String CURRENT_STRATEGY = PlannerStrategy.COMPONENTFLOWPATH_STRATEGY;
	
	private final Logger LOGGER = LogManager.getLogger(ComponentFlowPathStrategy.class);
	
	@Override
	public Integer execute(PlannerContext plannerContext, String part, String bomNumber, Integer requestedQty,
	                boolean findMaxAvailableQty) {

		Network network = plannerContext.getNetwork(part);
		int committedQty = 0;

		if (network != null) {
			Iterator<String> componentFlowMapIterator = network.getComponentFlows().keySet().iterator();
			while (componentFlowMapIterator.hasNext()) {
				String componentBomNumber = componentFlowMapIterator.next();
				ComponentFlow componentFlow = network.getComponentFlow(componentBomNumber);
				committedQty += executeComponentFlow(plannerContext, componentFlow, requestedQty, findMaxAvailableQty);
				if (committedQty == requestedQty) {
					break;
				}
			}
		}
		LOGGER.debug((findMaxAvailableQty ? "Searching - " : "") + String.format("Component Flow Path Strategy : Part - %1s, Requested - %2s, Committed - %3s", part,
		                requestedQty, committedQty));

		return committedQty;
	}

	/**
	 * This method is used to execute flows that need all components. If one of
	 * the component has 0 inventory, then we should not proceed.
	 * 
	 * @param plannerContext
	 * @param part
	 * @param requestedQty
	 * @param searchMode
	 * @param committedQty
	 * @param componentFlow
	 * @param componentFlows
	 * @return
	 */
	protected Integer executeComponentFlow(PlannerContext plannerContext, ComponentFlow componentFlow,
	                Integer requestedQty, boolean searchMode) {
		String bomNumber = componentFlow.getBomNumber();
		Map<String, Double> componentFlows = componentFlow.getComponents();
		int maxAvailableQty = getMaximumAvailableQty(plannerContext, bomNumber, requestedQty,
		                componentFlow);
		int committedQty = 0;
		double productionFactor = 1;

		if (maxAvailableQty > 0) {
			if (!searchMode) {
				requestedQty = Math.min(maxAvailableQty, requestedQty);
			}
			Iterator<String> componentIterator = componentFlows.keySet().iterator();
			while (componentIterator.hasNext()) {
				String componentPart = componentIterator.next();
				productionFactor = componentFlows.get(componentPart).doubleValue();
				int componentRequestQty = (int) Math.round(requestedQty * productionFactor);

				committedQty = StrategyExecutor.executeStrategy(plannerContext, CURRENT_STRATEGY,
				                bomNumber, componentPart, componentRequestQty, null, searchMode);
				if (committedQty == 0) {
					break;
				}
			}
			if (!searchMode) {
				componentFlow.setMaximumComponentQty(maxAvailableQty - (int) (committedQty / productionFactor));
			}
		}
		return committedQty;
	}

	/**
	 * This method is used to get minimum qty that can be satisfied for a list of components. (AND)
	 * @param plannerContext
	 * @param part
	 * @param level
	 * @param requestedQty
	 * @param committedQty
	 * @param componentFlows
	 * @return
	 */
	private int getMaximumAvailableQty(PlannerContext plannerContext, String bomNumber, Integer requestedQty, ComponentFlow componentFlow) {
		if (componentFlow.getMaximumComponentQty() != null) {
			return componentFlow.getMaximumComponentQty();
		} else {
			return calculateMaximumComponentQty(plannerContext, bomNumber, requestedQty, componentFlow);
		}
	}

	/**
	 * This method is used to get maximum qty that can be satisfied for a list of components. (AND)
	 * @param plannerContext
	 * @param bomNumber
	 * @param requestedQty
	 * @param componentFlow
	 * @return
	 */
	private int calculateMaximumComponentQty(PlannerContext plannerContext, String bomNumber, Integer requestedQty,
	                ComponentFlow componentFlow) {
		Map<String, Double> componentFlows = componentFlow.getComponents();
		Iterator<String> componentIterator = componentFlows.keySet().iterator();
		Integer maxAvailableQty = Integer.MAX_VALUE;
		int committedQty = 0;
		while (componentIterator.hasNext()) {
			String componentPart = componentIterator.next();
			double productionFactor = componentFlows.get(componentPart).doubleValue();
			int componentRequestQty = (int) Math.round(requestedQty * productionFactor);
			committedQty = StrategyExecutor.executeStrategy(plannerContext, CURRENT_STRATEGY,
			                bomNumber, componentPart, componentRequestQty, null, true);
			if (committedQty == 0) { // Do not proceed further as we have a
			                         // component having 0 inventory.
				maxAvailableQty = 0;
				break;
			}
			LOGGER.debug(String.format("Maximum Qty for Component : Part - %1s, Max Qty - %2s, Available Qty - %3s ", componentPart,
							maxAvailableQty, committedQty));
		}
		componentFlow.setMaximumComponentQty(maxAvailableQty);
		
		return Math.min(maxAvailableQty, committedQty);
	}
}
