package com.planning.core;

import com.planning.common.context.PlannerContext;

/**
 * Super type that holds Planning engine.
 * @author Karthik
 *
 */
public interface PlanningEngine {
	/**
	 * This method is used to import data on to planning entities.
	 * @param plannerContext
	 * @throws Exception
	 */
	void importData(PlannerContext plannerContext) throws Exception;
	/**
	 * This method is used to plan demands.
	 * @param plannerContext
	 */
	void performPlanning(PlannerContext plannerContext);
	/**
	 * This method is used to export demand plans.
	 * @param plannerContext
	 */
	void exportPlanData(PlannerContext plannerContext);
}
