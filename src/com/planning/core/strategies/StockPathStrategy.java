package com.planning.core.strategies;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.planning.common.context.PlannerContext;
import com.planning.common.model.input.Supply;
import com.planning.common.model.profiles.Network;
import com.planning.common.model.profiles.PlanPath;
import com.planning.core.service.StrategyExecutor;
/**
 * This strategy is responsible for reserving stocks on a part-depot.
 * @author Karthik
 *
 */
public class StockPathStrategy implements PlannerStrategy {

	public static final String CURRENT_STRATEGY = PlannerStrategy.STOCKPATH_STRATEGY;

	private final Logger LOGGER = LogManager.getLogger(StockPathStrategy.class);

	@Override
	public Integer execute(PlannerContext plannerContext, String part, String bomNumber, Integer requestedQty, boolean searchOnly) {
		List<Supply> supplies = plannerContext.getInventoryProfile(part);
		Network network = plannerContext.getNetwork(part);
		int committedQty = 0;
		if (requestedQty > 0) {
			if (supplies != null && !supplies.isEmpty()) {
				int availableQty = supplies.stream().mapToInt(supply -> supply.getQuantity() - supply.getCommittedQty()).sum();
				if (availableQty > 0) {
					if (availableQty >= requestedQty) {
						committedQty = requestedQty;
					} else {
						committedQty = availableQty;
					}
					if (!searchOnly) {
						supplies.get(0).setCommittedQty(committedQty);
						PlanPath planPath = new PlanPath(part, requestedQty, committedQty, null);
						plannerContext.addPlanPath(bomNumber, planPath);
					}
				}
			}
			if (!searchOnly) {
				LOGGER.info(String.format("Stock Path Strategy : Part - %1s, Requested - %2s, Committed - %3s", part,
				                requestedQty, committedQty));
			}
		
			if (network != null && committedQty < requestedQty) {
				int shortQty = requestedQty - committedQty;
				committedQty = StrategyExecutor.executeStrategy(plannerContext, CURRENT_STRATEGY, bomNumber, part, shortQty, null,
								searchOnly);
			}
		}

		return committedQty;
	}
}
