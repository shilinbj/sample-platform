package com.klaus.saas.commons.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagingRequest<T> {

	private T param;
	private MyPage<T> myPage;

}
