package com.klaus.saas.system.server.dao;

import com.klaus.saas.system.server.context.SessionContext;
import com.klaus.saas.system.server.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleDao {

	private final DatabaseClient client;
	private final R2dbcEntityTemplate template;

	public Mono<RoleEntity> save(RoleEntity entity) {
		return SessionContext.userId()
				.flatMap(currentUserId -> {
					entity.setCreatedBy(currentUserId);
					return template.insert(RoleEntity.class).using(entity);
				});
	}

	public Mono<Long> deleteById(long id) {
		return template.delete(RoleEntity.class).matching(Query.query(where("id").is(id))).all();
	}

	public Mono<RoleEntity> findById(long id) {
		return template.selectOne(Query.query(where("id").is(id)), RoleEntity.class);
	}

}
