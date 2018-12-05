package com.planning.core.factories;

import com.planning.core.strategies.ComponentFlowPathStrategy;
import com.planning.core.strategies.ComponentStockPathStrategy;
import com.planning.core.strategies.PlannerStrategy;
import com.planning.core.strategies.StockPathStrategy;

/**
 * This class is responsible for creating strategies.
 * @author Karthik
 *
 */
public class PlannerStrategyFactory {

	public static PlannerStrategy getStrategy(String strategyName) {
		switch(strategyName) {
			case PlannerStrategy.STOCKPATH_STRATEGY:
				return new StockPathStrategy();
			case PlannerStrategy.COMPONENTSTOCKPATH_STRATEGY:
				return new ComponentStockPathStrategy();
			case PlannerStrategy.COMPONENTFLOWPATH_STRATEGY:
				return new ComponentFlowPathStrategy();
			default:
				return null;
		}
	}
	
}
