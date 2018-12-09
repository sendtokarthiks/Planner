package com.planning.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.planning.common.context.PlannerContext;

/**
 * This class is used to start the planning engine.
 * @author Karthik
 *
 */
public class Planner {
	
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.planning");
		context.refresh();

		PlanningEngine planningEngine = context.getBean(PlanningEngine.class);
		PlannerContext plannerContext = context.getBean(PlannerContext.class);
		planningEngine.importData(plannerContext);
		planningEngine.plan(plannerContext);
		planningEngine.exportPlanData(plannerContext);

		context.close();
	}

}
