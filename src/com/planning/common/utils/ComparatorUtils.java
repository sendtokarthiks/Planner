package com.planning.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class holds utility functions.
 * @author Karthik
 *
 */
public class ComparatorUtils {
	public static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^([0-9]+)([a-z]?)$");

	public static int getSortOrder(String s) {
		Matcher m = ALPHANUMERIC_PATTERN.matcher(s);
		if (!m.matches()) return 0;
		int major = Integer.parseInt(m.group(1));
		int minor = m.group(2).isEmpty() ? 0 : m.group(2).charAt(0);
		return (major << 8) | minor;
	}
}
