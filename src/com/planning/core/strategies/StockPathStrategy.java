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
	public Integer execute(PlannerContext plannerContext, String part, String bomNumber, Integer requestedQty, boolean findMaxAvailableQty) {
		List<Supply> supplies = plannerContext.getInventoryProfile(part);
		Network network = plannerContext.getNetwork(part);
		int committedQty = 0;
		int availableQty = 0;
		if (requestedQty > 0) {
			if (supplies != null && !supplies.isEmpty()) {
				availableQty = supplies.stream().mapToInt(supply -> supply.getQuantity() - supply.getCommittedQty()).sum();
				if (findMaxAvailableQty) {
					committedQty = availableQty;
				} else if (availableQty > 0) {
					if (availableQty >= requestedQty) {
						committedQty = requestedQty;
					} else {
						committedQty = availableQty;
					}
					supplies.get(0).setCommittedQty(committedQty);
					PlanPath planPath = new PlanPath(part, requestedQty, committedQty, null);
					plannerContext.addPlanPath(bomNumber, planPath);
				}
			}

			if (findMaxAvailableQty) {
				LOGGER.debug("Searching - " + String.format("Stock Path Strategy : Part - %1s, Requested - %2s, Available - %3s, Committed - %4s", part,
				                requestedQty, availableQty, committedQty));
			} else {
				LOGGER.info(String.format("Part - %1s, Requested - %2s, Committed - %4s", part,
				                requestedQty, committedQty));
			}
		
			if (network != null && (findMaxAvailableQty || committedQty < requestedQty)) {
				int shortQty = requestedQty - committedQty;
				committedQty += StrategyExecutor.executeStrategy(plannerContext, CURRENT_STRATEGY, bomNumber, part, shortQty, null,
								findMaxAvailableQty);
			}
		}

		return committedQty;
	}
}
