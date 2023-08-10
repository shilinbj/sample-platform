package com.klaus.saas.system.server.vo;

import com.klaus.saas.commons.model.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Klaus
 * @since 2023/7/27
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Schema(name = "UserRole", description = "用户角色关系")
public class UserRole extends BaseVO {

	private long userId;
	private List<Long> roleIds;

}
