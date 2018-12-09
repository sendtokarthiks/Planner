package com.planning.core.strategies;

import com.planning.common.context.PlannerContext;
/**
 * Type for all planner strategies.
 * @author Karthik
 *
 */
public interface PlannerStrategy {
	public static final String STOCKPATH_STRATEGY = "STOCKPATH";
	public static final String COMPONENTSTOCKPATH_STRATEGY = "COMPONENTSTOCKPATH";
	public static final String COMPONENTFLOWPATH_STRATEGY = "COMPONENTFLOWPATH";
	public static final String ALTERNATEPATH_STRATEGY = "ALTERNATEFLOWPATH";
	public static final String MAXSTOCKPATH_STRATEGY = "MAXSTOCKPATH";

	public Integer execute(PlannerContext plannerContext, String part, String bomNumber, Integer requestedQty, boolean findMaxAvailableQty);
}
