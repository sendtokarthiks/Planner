package com.planning.core.service;

import static com.planning.common.Constants.BOM_DATA;
import static com.planning.common.Constants.DEMAND_DATA;
import static com.planning.common.Constants.SUPPLY_DATA;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.planning.common.context.PlannerContext;
import com.planning.common.model.input.BillOfMaterials;
import com.planning.common.model.input.Demand;
import com.planning.common.model.input.Supply;
import com.planning.common.model.profiles.Network;
import com.planning.core.importer.ConcreteDataImporter;

@Service
public class DataImporterService {

	/**
	 * This method is the entry point for importing data.
	 * @param plannerContext
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void importData(PlannerContext plannerContext) throws JsonParseException, JsonMappingException, IOException {
		importSupply(plannerContext);
		importDemand(plannerContext);
		importNetwork(plannerContext);
	}

	/**
	 * This method imports supply information.
	 * @param plannerContext
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private void importSupply(PlannerContext plannerContext) throws JsonParseException, JsonMappingException, IOException {
		Supply[] supplies = new ConcreteDataImporter<Supply[]>().importData(SUPPLY_DATA, Supply[].class);
		if (supplies != null) {
			for (Supply supply : supplies) {
				plannerContext.addSupply(supply);
			}
		}
	}
	
	/**
	 * This method imports demand information.
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private void importDemand(PlannerContext plannerContext)  throws JsonParseException, JsonMappingException, IOException {
		Demand[] demands = new ConcreteDataImporter<Demand[]>().importData(DEMAND_DATA, Demand[].class);
		if (demands != null) {
			Arrays.sort(demands);
			for (Demand demand : demands) {
				plannerContext.addDemand(demand);
			}
		}
	}
	

	/**
	 * This method is used to set built network to plannercontext.
	 * @param plannerContext
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	private void importNetwork(PlannerContext plannerContext) throws JsonParseException, JsonMappingException, IOException {
		BillOfMaterials[] boms = new ConcreteDataImporter<BillOfMaterials[]>().importData(BOM_DATA, BillOfMaterials[].class);
		if (boms != null && boms.length > 0) {
			Arrays.sort(boms);

			Map<String, Network> networks = plannerContext.getNetworks();
			
			for(BillOfMaterials bom : boms) {
				if (networks.containsKey(bom.getPart())) {
					buildNetwork(bom, networks.get(bom.getPart()));
				}
				else {
					Network network = new Network();
					network.setPart(bom.getPart());
					plannerContext.addNetwork(network);
					buildNetwork(bom, network);
				}
			}
		}
	}

	/**
	 * This method is used to build the network.
	 * @param bom
	 * @param network
	 */
	private void buildNetwork(BillOfMaterials bom, Network network) {
		network.addComponentFlow(bom.getComponent(), bom.getBomNumber(), bom.getProductionFactor());
	}

}
