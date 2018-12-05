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
	                boolean searchOnly) {

		Network network = plannerContext.getNetwork(part);
		int committedQty = 0;

		if (network != null) {
			Iterator<String> componentFlowMapIterator = network.getComponentFlows().keySet().iterator();
			while (componentFlowMapIterator.hasNext()) {
				String componentBomNumber = componentFlowMapIterator.next();
				ComponentFlow componentFlow = network.getComponentFlow(componentBomNumber);
				committedQty += executeComponentFlow(plannerContext, componentFlow, requestedQty, searchOnly);
				if (committedQty == requestedQty) {
					break;
				}
			}
		}
		if (!searchOnly) {
			LOGGER.debug(String.format("Component Flow Path Strategy : Part - %1s, Requested - %2s, Committed - %3s", part,
			                requestedQty, committedQty));
		}

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
		int minAvailableQty = getMinimumAvailableQty(plannerContext, bomNumber, requestedQty,
		                componentFlows);
		int committedQty = 0;

		if (minAvailableQty > 0) {
			Iterator<String> componentIterator = componentFlows.keySet().iterator();
			while (componentIterator.hasNext()) {
				String componentPart = componentIterator.next();
				double productionFactor = componentFlows.get(componentPart).doubleValue();
				int componentRequestQty = (int) Math.round(minAvailableQty * productionFactor);

				committedQty = StrategyExecutor.executeStrategy(plannerContext, CURRENT_STRATEGY,
				                bomNumber, componentPart, componentRequestQty, null, searchMode);
				if (committedQty == 0) {
					break;
				}
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
	private int getMinimumAvailableQty(PlannerContext plannerContext, String bomNumber, Integer requestedQty, Map<String, Double> componentFlows) {
		Iterator<String> componentIterator = componentFlows.keySet().iterator();
		Integer minCommittedQty = Integer.MAX_VALUE;
		int committedQty = 0;
		while (componentIterator.hasNext()) {
			String componentPart = componentIterator.next();
			double productionFactor = componentFlows.get(componentPart).doubleValue();
			int componentRequestQty = (int) Math.round(requestedQty * productionFactor);
			committedQty = StrategyExecutor.executeStrategy(plannerContext, CURRENT_STRATEGY,
			                bomNumber, componentPart, componentRequestQty, null, true);
			if (committedQty == 0) { // Do not proceed further as we have a
			                         // component having 0 inventory.
				minCommittedQty = 0;
				break;
			} else if (committedQty < minCommittedQty) {
				minCommittedQty = (int) (committedQty / productionFactor);
			}
			
			LOGGER.debug(String.format("Minimum Qty for Component : Part - %1s, Min Qty - %2s, Committed Qty - %3s ", componentPart,
							minCommittedQty, committedQty));
		}
		
		return Math.min(minCommittedQty, committedQty);
	}
}
