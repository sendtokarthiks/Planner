package com.planning.common.model.output;

import java.util.Date;
import com.planning.common.model.Entity;

/**
 * This class holds the committed inventory for every Part-Depot.
 * @author Karthik
 *
 */
public class DemandPlan implements Entity {

	@Override
	public String getId() {
		return orderNumber + PRIMARYKEY_JOINER + planSequenceNumber;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Integer getPlanSequenceNumber() {
		return planSequenceNumber;
	}

	public void setPlanSequenceNumber(Integer planSequenceNumber) {
		this.planSequenceNumber = planSequenceNumber;
	}
	
	public Integer getRequestedQty() {
		return requestedQty;
	}

	public void setRequestedQty(Integer requestedQty) {
		this.requestedQty = requestedQty;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	private String orderNumber;
	private Integer planSequenceNumber;
	private String part;
	private Integer requestedQty;
	private Integer quantity;
	private Date scheduledDate;

}
