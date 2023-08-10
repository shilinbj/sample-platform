package com.klaus.saas.commons.entity;

import com.klaus.saas.commons.model.BaseVO;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;

/**
 * @author shilin
 * @since 2020-03-02
 */
@Data
@Accessors(chain = true)
public abstract class BaseEntity {

	@Column("created_by")
	private long createdBy;
	@Column("created_name")
	private String createdName;
	@Column("created_at")
	private LocalDateTime createdAt;
	@Column("modifyed_by")
	private long modifyedBy;
	@Column("modifyed_name")
	private String modifyedName;
	@Column("modifyed_at")
	private LocalDateTime modifyedAt;

	public <T extends BaseVO> T toVO() {
		BaseVO vo = convert();
		vo.setCreatedBy(this.createdName);
		vo.setCreatedAt(this.createdAt);
		vo.setModifiedBy(this.modifyedName);
		vo.setModifiedAt(this.modifyedAt);
		return (T) vo;
	}

	protected abstract BaseVO convert();

}
