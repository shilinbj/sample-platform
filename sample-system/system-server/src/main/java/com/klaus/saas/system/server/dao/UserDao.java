package com.klaus.saas.system.server.dao;

import com.klaus.saas.system.server.entity.UserEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

import static org.springframework.data.relational.core.query.Criteria.where;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class UserDao extends BaseDao {

	private final DatabaseClient client;
	private final R2dbcEntityTemplate template;

	private final BiFunction<Row, RowMetadata, UserEntity> USER_MAPPING = (row, rowMetadata) -> {
		UserEntity entity = new UserEntity();
		mappingValues(row, rowMetadata, UserEntity.class, entity);
		return entity;
	};

	public Mono<UserEntity> findByUsername(String username) {
		return template.selectOne(Query.query(
				where("username").is(username)
						.and("status").not("3")
		), UserEntity.class);
	}

}
