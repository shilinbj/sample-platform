package com.klaus.saas.system.server.dao;

import com.klaus.saas.system.server.context.SessionContext;
import com.klaus.saas.system.server.entity.ResourceEntity;
import com.klaus.saas.system.server.entity.RoleEntity;
import com.klaus.saas.system.server.vo.RoleResource;
import com.klaus.saas.system.server.vo.UserRole;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class AuthDao extends BaseDao {

	private final DatabaseClient client;
	private final R2dbcEntityTemplate template;

	private final BiFunction<Row, RowMetadata, ResourceEntity> RESOURCE_MAPPING = (row, rowMetadata) -> {
		ResourceEntity entity = new ResourceEntity();
		mappingValues(row, rowMetadata, ResourceEntity.class, entity);
		return entity;
	};

	private final BiFunction<Row, RowMetadata, RoleEntity> ROLE_MAPPING = (row, rowMetadata) -> {
		RoleEntity entity = new RoleEntity();
		mappingValues(row, rowMetadata, RoleEntity.class, entity);
		return entity;
	};

	public Mono<Long> saveRoleResource(RoleResource rr) {
		String sql = "insert into sys_role_resource(role_id, resource_id, created_by) values (:roleId, :resourceId, :createdBy)";
		return SessionContext.userId()
				.flatMap(userId -> {
					Long roleId = rr.getRoleId();
					return Flux.fromIterable(rr.getResourceId())
							.flatMap(resourceId ->
									client.sql(sql)
											.bind("roleId", roleId)
											.bind("resourceId", resourceId)
											.bind("createdBy", userId)
											.fetch().rowsUpdated()).count();
				});
	}

	public Mono<Long> deleteRoleResource(long roleId) {
		String sql = "delete from sys_role_resource where role_id = :role_id";
		return client.sql(sql)
				.bind("role_id", roleId)
				.fetch().rowsUpdated();
	}

	public Flux<Long> findRoleIdByResourceId(long resourceId) {
		String sql = "select distinct role_id from sys_role_resource where resource_id = :resource_id";
		return client.sql(sql)
				.bind("resource_id", resourceId)
				.map(row -> row.get("role_id", Long.class))
				.all();
	}

	public Mono<Long> saveUserRole(UserRole ur) {
		String sql = "insert into sys_user_role(user_id, role_id, created_by) values (:userId, :roleId, :createdBy)";
		return SessionContext.userId()
				.flatMap(currentUserId -> {
					Long userId = ur.getUserId();
					return Flux.fromIterable(ur.getRoleIds())
							.flatMap(roleId ->
									client.sql(sql)
											.bind("userId", userId)
											.bind("roleId", roleId)
											.bind("createdBy", currentUserId)
											.fetch().rowsUpdated()).count();
				});
	}

	public Mono<Long> deleteUserRoleByUserId(long userId) {
		String sql = "delete from sys_user_role where user_id = :userId";
		return client.sql(sql)
				.bind("userId", userId)
				.fetch().rowsUpdated();
	}

	public Flux<String> findUserResource(Long userId) {
		String sql = """
			select c.resource as resource
			from sys_user_role a
			         left join sys_role_resource b on a.role_id = b.role_id
			         left join sys_resource c on b.resource_id = c.id
			where a.user_id = :userId
				""";
		return client.sql(sql)
				.bind("userId", userId)
				.map(row -> row.get("resource", String.class))
				.all();
	}

	public Flux<Map<String, Object>> findAllUserResource() {
		String sql = """
			select a.user_id, c.resource
			from sys_user_role a
			         left join sys_role_resource b on a.role_id = b.role_id
			         left join sys_resource c on b.resource_id = c.id
				""";
		return client.sql(sql).map((row) -> {
			Map<String, Object> map = new HashMap<>();
			map.put("user_id", row.get("user_id", Long.class));
			map.put("resource", row.get("resource", String.class));
			return map;
		}).all();
	}

	public Flux<Long> findUserIdByRoleId(Long roleId) {
		String sql = "select user_id from sys_user_role where role_id = :role_id";
		return client.sql(sql)
				.bind("role_id", roleId)
				.map(row -> row.get("user_id", Long.class))
				.all();
	}

}
