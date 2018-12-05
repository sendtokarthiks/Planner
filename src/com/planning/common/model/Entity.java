package com.planning.common.model;

/**
 * This is the super type for all entities. 
 * @author Karthik
 *
 */
public interface Entity {
	public static final String PRIMARYKEY_JOINER = " - ";
	public String getId();
}
