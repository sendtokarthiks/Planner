package com.planning.core.service;

import com.planning.common.context.PlannerContext;
import com.planning.common.model.profiles.Network;
import com.planning.core.factories.PlannerStrategyFactory;
import com.planning.core.strategies.PlannerStrategy;
/**
 * This class is responsible for executing strategies.
 * @author Karthik
 *
 */
public class StrategyExecutor {

	/**
	 * This method is used to execute strategies based on previously executed strategy and few more conditions.
	 * @param plannerContext
	 * @param previousStrategy
	 * @param bomNumber
	 * @param part
	 * @param requestedQty
	 * @param forceStrategy
	 * @param findMaxAvailableQty
	 * @return
	 */
	public static Integer executeStrategy(PlannerContext plannerContext, String previousStrategy, String bomNumber,
	                String part, Integer requestedQty, String forceStrategy, boolean findMaxAvailableQty) {
		Network network = plannerContext.getNetwork(part);
		PlannerStrategy nextStrategy = null;
		int committedQty = 0;

		if (forceStrategy != null) {
			nextStrategy = PlannerStrategyFactory.getStrategy(forceStrategy);
			committedQty = nextStrategy.execute(plannerContext, part, bomNumber, requestedQty, findMaxAvailableQty);
		} else if (previousStrategy == null || network == null) {
			nextStrategy = PlannerStrategyFactory.getStrategy(PlannerStrategy.STOCKPATH_STRATEGY);
			committedQty = nextStrategy.execute(plannerContext, part, bomNumber, requestedQty, findMaxAvailableQty);
		} else if (previousStrategy.equals(PlannerStrategy.STOCKPATH_STRATEGY) && !network.getComponentFlows().isEmpty()) {
			nextStrategy = PlannerStrategyFactory.getStrategy(PlannerStrategy.COMPONENTSTOCKPATH_STRATEGY);
			committedQty = nextStrategy.execute(plannerContext, part, bomNumber, requestedQty, findMaxAvailableQty);
		} else if (previousStrategy.equals(PlannerStrategy.COMPONENTSTOCKPATH_STRATEGY) && !network.getComponentFlows().isEmpty()) {
			nextStrategy = PlannerStrategyFactory.getStrategy(PlannerStrategy.COMPONENTFLOWPATH_STRATEGY);
			committedQty = nextStrategy.execute(plannerContext, part, bomNumber, requestedQty, findMaxAvailableQty);
		}  else if (previousStrategy.equals(PlannerStrategy.COMPONENTFLOWPATH_STRATEGY)) {
			nextStrategy = PlannerStrategyFactory.getStrategy(PlannerStrategy.STOCKPATH_STRATEGY);
			committedQty = nextStrategy.execute(plannerContext, part, bomNumber, requestedQty, findMaxAvailableQty);
		}

		return committedQty;
	}
}
