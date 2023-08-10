package com.klaus.saas.system.server.dao;

import com.klaus.saas.commons.model.MyPage;
import com.klaus.saas.system.model.vo.User;
import com.klaus.saas.system.server.entity.UserEntity;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
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

	public Mono<UserEntity> save(UserEntity entity) {
		return template.insert(UserEntity.class).using(entity);
	}

	public Mono<Long> modifyPassword(String username, String password) {
		return template.update(
				Query.query(where("username").is(username)),
				Update.update("password", password),
				UserEntity.class);
	}

	public Mono<UserEntity> findByUsername(String username) {
		return template.selectOne(Query.query(
				where("username").is(username)
		), UserEntity.class);
	}

	public Flux<UserEntity> findAll() {
		return template.select(UserEntity.class).all();
	}

	public Mono<Long> findCount(User user) {
		String sql = """
			select count(1)
			from sys_user a
			where if(:username is not null and :username != '', a.username = :username, 1 = 1)
			  and if(:name is not null and :name != '', a.name like concat('%', :name, '%'), 1 = 1)
			  and if(:email is not null and :email != '', a.email like concat('%', :email, '%'), 1 = 1)
			  and if(:mobile is not null and :name != '', a.mobile = :mobile, 1 = 1)
				""";
		return client.sql(sql)
				.bind("username", user.getUsername())
				.bind("name", user.getName())
				.bind("email", user.getEmail())
				.bind("mobile", user.getMobile())
				.map((row, rowMetadata) -> row.get("count", Long.class))
				.one();
	}

	public Flux<UserEntity> findPage(User user, MyPage<?> page) {
		String sql = """
			select a.id,
			       a.username,
			       a.name,
			       a.sex,
			       a.email,
			       a.mobile,
			       a.status,
			       a.created_by,
			       a.created_at,
			       a.modified_by,
			       a.modified_at,
			       b.name as created_name,
			       c.name as modified_name
			from sys_user a
			         left join sys_user b on a.created_by = b.id
			         left join sys_user c on a.modified_by = c.id
			where if(:username is not null and :username != '', a.username = :username, 1 = 1)
			  and if(:name is not null and :name != '', a.name like concat('%', :name, '%'), 1 = 1)
			  and if(:email is not null and :email != '', a.email like concat('%', :email, '%'), 1 = 1)
			  and if(:mobile is not null and :name != '', a.mobile = :mobile, 1 = 1)
			limit :offset, :size
				""";
		return client.sql(sql)
				.bind("username", user.getUsername())
				.bind("name", user.getName())
				.bind("email", user.getEmail())
				.bind("mobile", user.getMobile())
				.bind("offset", page.getOffset())
				.bind("size", page.getSize())
				.map(USER_MAPPING)
				.all();
	}

}
