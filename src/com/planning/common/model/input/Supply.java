package com.planning.common.model.input;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.planning.common.model.Entity;
/**
 * This entity stores supply information. Supply could be inventory or material from supplier.
 * @author Karthik
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "part", "type", "quantity", "availableDate" })
public class Supply implements Entity {
	@Override
	public String getId() {
		return part + PRIMARYKEY_JOINER + type + PRIMARYKEY_JOINER + availableDate;
	}

	@JsonProperty("part")
	public String getPart() {
		return part;
	}

	@JsonProperty("part")
	public void setPart(String part) {
		this.part = part;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("quantity")
	public Integer getQuantity() {
		return quantity;
	}

	@JsonProperty("quantity")
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@JsonProperty("availableDate")
	public Date getAvailableDate() {
		return availableDate;
	}

	@JsonProperty("availableDate")
	public void setAvailableDate(Date availableDate) {
		this.availableDate = availableDate;
	}
	public Integer getCommittedQty() {
		return committedQty;
	}

	public void setCommittedQty(Integer committedQty) {
		this.committedQty = committedQty;
	}

	@JsonProperty("part")
	private String part;
	@JsonProperty("type")
	private String type;
	@JsonProperty("quantity")
	private Integer quantity;
	@JsonProperty("availableDate")
	private Date availableDate;
	private Integer committedQty = 0;
}