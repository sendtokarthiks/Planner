package com.planning.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.planning.common.Constants;
import com.planning.common.context.PlannerContext;
import com.planning.common.model.input.Demand;
import com.planning.common.model.output.DemandPlan;
import com.planning.common.model.profiles.PlanPath;
import com.planning.common.utils.CSVWriter;
import com.planning.core.service.DataImporterService;
import com.planning.core.service.StrategyExecutor;

@Component
public class PlanningEngineImpl implements PlanningEngine {

	private final Logger LOGGER = LogManager.getLogger(PlanningEngineImpl.class);

	@Autowired
	DataImporterService dataImporterService;

	@Override
	public void importData(PlannerContext plannerContext) throws Exception {
		dataImporterService.importData(plannerContext);
		LOGGER.info("Import Complete..");
	}

	@Override
	public void plan(PlannerContext plannerContext) {
		for (Demand demand : plannerContext.getDemands()) {
			LOGGER.info(String.format("Planning demand %1s , Qty : %2s", demand.getOrderNumber(), demand.getQuantity()));
			int committedQty = StrategyExecutor.executeStrategy(plannerContext, null, demand.getPart() + "-BOM1",
			                demand.getPart(), demand.getQuantity(), null, false);

			createDemandPlans(demand, plannerContext);
			updateDemandStatus(committedQty, demand.getQuantity(), demand);
			LOGGER.info(String.format("Demand : %1s, Status : %2s", demand.getOrderNumber(), demand.getStatus()));
		}
		LOGGER.info("Planning Complete..");
	}

	/**
	 * This method is used to update demand status based on committed qty.
	 * @param committedQty
	 * @param requestedQty
	 * @param demand
	 */
	private void updateDemandStatus(int committedQty, int requestedQty, Demand demand) {
		String demandStatus = Constants.DEMAND_STATUS_UNMET;

		if (requestedQty == committedQty) {
			demandStatus = Constants.DEMAND_STATUS_MET;
		} else if (committedQty > 0) {
			demandStatus = Constants.DEMAND_STATUS_SHORT;
		}
		demand.setStatus(demandStatus);
	}

	/**
	 * This method is used to create demand plans and update demand status based on the request and committed quantity.
	 * @param demand
	 * @param plannerContext
	 */
	private void createDemandPlans(Demand demand, PlannerContext plannerContext) {
		Map<String, Map<String, PlanPath>> allPlanPaths = plannerContext.getPlanPaths();
		Iterator<String> skuPlanPathIterator = allPlanPaths.keySet().iterator();
		int planSequenceNumber = 1;
		String demandStatus = Constants.DEMAND_STATUS_UNMET;
		while (skuPlanPathIterator.hasNext()) {
			Collection<PlanPath> planPaths = allPlanPaths.get(skuPlanPathIterator.next()).values();

			for (PlanPath planPath : planPaths) {
				DemandPlan demandPlan = new DemandPlan();
				demandPlan.setOrderNumber(demand.getOrderNumber());
				demandPlan.setPart(planPath.getPart());
				demandPlan.setPlanSequenceNumber(planSequenceNumber++);
				demandPlan.setRequestedQty(planPath.getRequestedQty());
				demandPlan.setQuantity(planPath.getCommittedQty());
				plannerContext.addDemandPlan(demandPlan);
			}
		}
		demand.setStatus(demandStatus);
		plannerContext.getPlanPaths().clear();
	}

	@Override
	public void exportPlanData(PlannerContext plannerContext) {
		Map<String, List<DemandPlan>> demandPlansMap = plannerContext.getDemandPlansMap();
		List<DemandPlan> list = new ArrayList<DemandPlan>(demandPlansMap.values().size());
		Iterator<String> demandPlansMapIterator = demandPlansMap.keySet().iterator();
		while (demandPlansMapIterator.hasNext()) {
			list.addAll(demandPlansMap.get(demandPlansMapIterator.next()));
		}
		CSVWriter.writeToCsv(list, "DemandPlans.csv");
		CSVWriter.writeToCsv(plannerContext.getDemands(), "Demands.csv");
		
		LOGGER.info("Export Complete..");
	}
}
