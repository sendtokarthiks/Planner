package com.planning.common.model.profiles;

import java.util.Date;
import com.planning.common.model.Entity;

/**
 * This entity holds the temporary plan for a demand.
 * @author Karthik
 *
 */
public class PlanPath implements Entity {

	public PlanPath(String part, Integer requestedQty, Integer committedQty, Date committedDate) {
		super();
		this.part = part;
		this.requestedQty = requestedQty;
		this.committedQty = committedQty;
		this.committedDate = committedDate;
	}

	@Override
	public String getId() {
		return part + PRIMARYKEY_JOINER + committedQty;
	}

	public String getPart() {
		return part;
	}

	public Integer getRequestedQty() {
		return requestedQty;
	}

	public Integer getCommittedQty() {
		return committedQty;
	}

	public void setCommittedQty(Integer committedQty) {
		this.committedQty = committedQty;
	}

	public Date getCommittedDate() {
		return committedDate;
	}

	private String part;
	private Integer committedQty;
	private Integer requestedQty;
	private Date committedDate;
}
