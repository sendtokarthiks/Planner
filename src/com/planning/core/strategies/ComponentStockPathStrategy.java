package com.planning.core.strategies;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.planning.common.Constants;
import com.planning.common.context.PlannerContext;
import com.planning.common.model.input.Supply;
import com.planning.common.model.profiles.ComponentFlow;
import com.planning.common.model.profiles.Network;
import com.planning.core.service.StrategyExecutor;
/**
 * This class does a breadth first search to identify stocks across alternate flows.
 * @author Karthik
 *
 */
public class ComponentStockPathStrategy implements PlannerStrategy {

	public static final String CURRENT_STRATEGY = PlannerStrategy.COMPONENTSTOCKPATH_STRATEGY;

	private final Logger LOGGER = LogManager.getLogger(ComponentStockPathStrategy.class);

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
				Map<String, Double> components = componentFlow.getComponents();
				Iterator<String> componentsIterator = components.keySet().iterator();
				int minimumAvailableStockQty = getMinimumAvailableStockQty(plannerContext, components, componentsIterator);
				int componentCommittedQty = commitStock(plannerContext, bomNumber, requestedQty, searchOnly, components,
				                minimumAvailableStockQty);
				committedQty += componentCommittedQty;
				if (committedQty == requestedQty) {
					break; // we have enough inventory.
				}
			}
		}
		if (!searchOnly) {
			LOGGER.debug(String.format("Component Stock Path Strategy : Part - %1s, Requested - %2s, Committed - %3s", part,
			                requestedQty, committedQty));
		}
		if (committedQty < requestedQty) {
			int shortQty = requestedQty - committedQty;
			committedQty = StrategyExecutor.executeStrategy(plannerContext, CURRENT_STRATEGY, bomNumber, part, shortQty,
			                null, searchOnly);
		}

		return committedQty;
	}

	/**
	 * This method is used to create plan path for a stock available in a component.
	 * @param plannerContext
	 * @param bomNumber
	 * @param requestedQty
	 * @param searchOnly
	 * @param componentFlow
	 * @param components
	 * @param minimumAvailableStockQty
	 * @return
	 */
	private int commitStock(PlannerContext plannerContext, String bomNumber, Integer requestedQty, boolean searchOnly,
	                Map<String, Double> components, int minimumAvailableStockQty) {
		int componentCommittedQty = 0;
		if (minimumAvailableStockQty > 0) {
			if (requestedQty < minimumAvailableStockQty) {
				// Do not request more than what is needed.
				minimumAvailableStockQty = requestedQty;
			}
			Iterator<String> componentsIteratorCommit = components.keySet().iterator();

			while (componentsIteratorCommit.hasNext()) {
				String componentPart = componentsIteratorCommit.next();
				Double productionFactor = components.get(componentPart);
				Integer componentRequestQty = minimumAvailableStockQty * productionFactor.intValue();
				componentCommittedQty = StrategyExecutor.executeStrategy(plannerContext, CURRENT_STRATEGY, bomNumber,
				                componentPart, componentRequestQty, PlannerStrategy.STOCKPATH_STRATEGY, searchOnly);
				componentCommittedQty = (int) (componentCommittedQty/ productionFactor);
			}
		}
		return componentCommittedQty;
	}

	/**
	 * This method calculates minimum stock qty that can be satisfied by the components.
	 * @param plannerContext
	 * @param components
	 * @param componentsIterator
	 * @return
	 */
	private int getMinimumAvailableStockQty(PlannerContext plannerContext, Map<String, Double> components,
	                Iterator<String> componentsIterator) {
		int minimumAvailableStockQty = Integer.MAX_VALUE;
		while (componentsIterator.hasNext()) {
			String componentPart = componentsIterator.next();
			Double productionFactor = components.get(componentPart);
			Integer componentStockQty = getStockQuantity(plannerContext.getInventoryProfile(componentPart), true);
			componentStockQty = (int) (componentStockQty / productionFactor);

			if (componentStockQty == 0) {
				minimumAvailableStockQty = 0;
				break;// Can't proceed further, as there is no inventory in one of the components.
			}
			if (componentStockQty < minimumAvailableStockQty) {
				minimumAvailableStockQty = componentStockQty;
			}
		}
		return minimumAvailableStockQty;
	}

	/**
	 * Get stock quantity based on inventory profile.
	 * @param supplies
	 * @param ignoreSupplies
	 * @return
	 */
	private Integer getStockQuantity(List<Supply> supplies, boolean ignoreSupplies) {
		int availableQty = 0;
		if (supplies != null && !supplies.isEmpty()) {
			for (Supply supply : supplies) {
				if (ignoreSupplies) {
					if (supply.getType().equals(Constants.STOCK_INVENTORY_TYPE)) {
						availableQty += supply.getQuantity() - supply.getCommittedQty();
					}
				} else {
					availableQty += supply.getQuantity() - supply.getCommittedQty();
				}
			}
		}
		return availableQty;
	}
}
