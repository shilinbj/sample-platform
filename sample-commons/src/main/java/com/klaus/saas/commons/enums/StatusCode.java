package com.klaus.saas.commons.enums;

import lombok.Getter;

/**
 * @author shilin
 * @since 2019-08-20
 */
@Getter
public enum StatusCode {

	OK(200, "success"),
	BAD_REQUEST_PARAM(400, "请求参数错误"),
	UN_AUTH(401, "认证失败!"),
	BAD_PASSWORD(402, "用户名或密码不正确!"),
	FORBIDDEN(403, "鉴权拒绝!"),
	NOT_FOUND(404, "资源无法找到!"),
	METHOD_NOT_ALLOWED(405, "请求行中指定的请求方法不能被用于请求相应的资源！"),
	NOT_ACCEPTABLE(406, "请求头不支持！"),
	REQUEST_TIMEOUT(408, "请求访问超时!"),
	SESSION_TIMEOUT(409, "超时，请重新登录"),
	PRECONDITION_FAILED(412, "前置条件不满足!"),
	UNSUPPORTED_MEDIA_TYPE(415, "对于当前请求的方法和所请求的资源，请求中提交的实体并不是服务器中所支持的格式!"),
	LOCKED(425, "资源被锁定!"),
	TOO_MANY_REQUESTS(429, "系统繁忙，请稍后再试!"),
	PARAMETER_NOTFOUND(431, "参数未找到数据!"),
	USER_STATE_STOPPED(450, "该用户已被停用"),
	USER_STATE_LOCKED(451, "该用户已被锁定"),
	INTERNAL_ERROR(500, "服务器内部错误!"),
	EXTERNAL_RESOURCE_ERROR(503, "服务资源调用异常!"),
	DB_DATA_ERROR(700, "数据库数据异常!"),
	ORDERLIMIT_EXHAUSTED(701, "订单限额查询量已耗尽!"),
	CONTRACT_NOT_EXECUTING(702, "客户合同状态不是执行中!"),
	COLUMN_EMPTY(703, "无有效的查询字段!"),
	ORDERLIMIT_NOT_FOUND(704, "无有效的订单限额信息!"),
	USER_IP_NOT_ALLOWED(801, "用户IP地址不在允许范围内!"),
	USER_DECRYPTED_FAILED(802, "请求签名信息解密失败!"),
	USER_UID_NOT_MATCHED(803, "用户UID和传入的签名不匹配!"),
	USER_TIMESTAMP_EXPIRED(804, "请求时间戳已过期!"),
	INFINITE_RESOURCE_LIMIT(901, "无限量资源访问");

	private final int code;
	private final String message;

	StatusCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
