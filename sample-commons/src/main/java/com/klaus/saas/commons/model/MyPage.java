package com.klaus.saas.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class MyPage<T> {

	private List<T> list;
	private long total;
	private int page;
	private int size;

	public MyPage(int page, int size) {
		this.page = page;
		this.size = size;
	}

	public int getOffset() {
		return (page - 1) * size;
	}

}