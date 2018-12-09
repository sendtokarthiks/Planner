package com.planning.common.model.input;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.planning.common.model.Entity;
import com.planning.common.utils.ComparatorUtils;

/**
 * This entity holds the list of bill of materials.
 * Entity records are sorted by Part, Priority, BOM and Sequence.
 * @author Karthik
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "bomNumber", "part", "productionFactor", "sequenceNumber", "component" })
public class BillOfMaterials implements Entity, Comparable<BillOfMaterials> {

	@JsonProperty("bomNumber")
	private String bomNumber;
	@JsonProperty("part")
	private String part;
	@JsonProperty("productionFactor")
	private Double productionFactor;
	@JsonProperty("sequenceNumber")
	private Integer sequenceNumber;
	@JsonProperty("component")
	private String component;

	@JsonProperty("bomNumber")
	public String getBomNumber() {
		return bomNumber;
	}

	@JsonProperty("bomNumber")
	public void setBomNumber(String bomNumber) {
		this.bomNumber = bomNumber;
	}

	@JsonProperty("part")
	public String getPart() {
		return part;
	}

	@JsonProperty("part")
	public void setPart(String part) {
		this.part = part;
	}

	@JsonProperty("productionFactor")
	public Double getProductionFactor() {
		return productionFactor;
	}

	@JsonProperty("productionFactor")
	public void setProductionFactor(Double productionFactor) {
		this.productionFactor = productionFactor;
	}

	@JsonProperty("sequenceNumber")
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	@JsonProperty("sequenceNumber")
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	@JsonProperty("component")
	public String getComponent() {
		return component;
	}

	@JsonProperty("component")
	public void setComponent(String component) {
		this.component = component;
	}

	@Override
	public String getId() {
		return bomNumber + PRIMARYKEY_JOINER + sequenceNumber;
	}
	
	@Override
    public int compareTo(BillOfMaterials compareBom) {
		//Sort by Part, BOM Number, Sequence number
		int bomSortOrder = ComparatorUtils.getSortOrder(part);
		int compareBomSortOrder = ComparatorUtils.getSortOrder(compareBom.getPart());
		int diff = bomSortOrder - compareBomSortOrder;
		if(diff != 0) {
			return diff;
		}

		bomSortOrder = ComparatorUtils.getSortOrder(bomNumber);
		compareBomSortOrder = ComparatorUtils.getSortOrder(compareBom.getBomNumber());
		diff = bomSortOrder - compareBomSortOrder;
		if(diff != 0) {
			return diff;
		}
		
		return sequenceNumber.compareTo(compareBom.getSequenceNumber());
		
    }

}