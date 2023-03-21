package com.cafe.storekeeper.helper.constant;

public final class ConstantsRest {

	private ConstantsRest() {
	}

	// SECURITY
	public static final String AUTHORIZATION = "Authorization";
	public static final String TOKEN_BASIC = "Basic ";
	public static final String TOKEN_BEARER = "Bearer ";
	public static final String GRANT_TYPE = "grant_type";
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	// PARAMS AND PAGINATION
	public static final String FIELDS = "fields";
	public static final String WHERE = "where";
	public static final String WHERE_NOT = "where_not";
	public static final String FROM = "from";
	public static final String LIKE = "like";
	public static final String TO = "to";
	public static final String SKIP = "skip";
	public static final String COUNT = "count";
	public static final String SORT = "sort";
	public static final String TYPE_SORT = "type_sort";
}