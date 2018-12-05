package com.planning.common.model.input;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.planning.common.model.Entity;

/**
 * This class holds all demand information.
 * @author Karthik
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "orderNumber", "part", "quantity", "priority", "needDate" })
public class Demand implements Entity, Comparable<Demand> {

	@JsonProperty("orderNumber")
	public String getOrderNumber() {
		return orderNumber;
	}

	@JsonProperty("orderNumber")
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	@JsonProperty("part")
	public String getPart() {
		return part;
	}

	@JsonProperty("part")
	public void setPart(String part) {
		this.part = part;
	}

	@JsonProperty("quantity")
	public Integer getQuantity() {
		return quantity;
	}

	@JsonProperty("quantity")
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@JsonProperty("priority")
	public Integer getPriority() {
		return priority;
	}

	@JsonProperty("priority")
	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	@JsonProperty("needDate")
	public Date getNeedDate() {
		return needDate;
	}

	@JsonProperty("needDate")
	public void setNeedDate(Date needDate) {
		this.needDate = needDate;
	}

	@Override
	public String getId() {
		return orderNumber + PRIMARYKEY_JOINER + part;
	}
	
	@Override
    public int compareTo(Demand compDemand) {
		return priority.compareTo(compDemand.getPriority());
		
    }
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty("orderNumber")
	private String orderNumber;
	@JsonProperty("part")
	private String part;
	@JsonProperty("quantity")
	private Integer quantity;
	@JsonProperty("priority")
	private Integer priority;
	@JsonProperty("needDate")
	private Date needDate;
	private String status;
}