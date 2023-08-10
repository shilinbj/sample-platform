package com.klaus.saas.system.server.constants;

/**
 * @author shilin
 * @since 2019-09-17
 */
public class AuthConstants {

	/**
	 * JWT claim username.
	 */
	public static final String CLAIM_USERNAME = "username";

	// **************************************** redis prefix ****************************************
	/**
	 * User jwt token redis key prefix.
	 */
	public static final String BEARER_PREFIX = "Bearer_";

	/**
	 * key: username, value: Set<String> resource
	 * 用于登录时校验用户是否拥有该资源权限
	 */
	public static final String AUTH_USER_RESOURCE_PRE = "aur-";

	/**
	 * key: username, value: Set<Resource>
	 * 保存用户所拥有的全部Resource对象, 用于用户登录后初始化页面菜单
	 */
	public static final String USER_RESOURCE_PRE = "ur-";

	/**
	 * 用户基本信息缓存
	 */
	public static final String USER_PRE = "user_";

	/**
	 * 客户基本信息缓存
	 */
	public static final String CUSTOMER_PRE = "cust-";

	/**
	 * 资源基本信息缓存
	 */
	public static final String RESOURCE_PRE = "res-";

	/**
	 * 用户接口限量
	 * key: custcode, value: limit
	 */
	public static final String CUST_RES_LIMIT_PRE = "crl-";
	// **************************************** redis prefix ****************************************

}
