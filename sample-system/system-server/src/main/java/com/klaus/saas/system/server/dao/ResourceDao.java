package com.klaus.saas.system.server.dao;

import com.klaus.saas.system.server.context.SessionContext;
import com.klaus.saas.system.server.entity.ResourceEntity;
import com.klaus.saas.system.server.vo.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.relational.core.query.Criteria.where;

/**
 * @author Klaus
 * @since 2023/8/10
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ResourceDao {

	private final DatabaseClient client;
	private final R2dbcEntityTemplate template;

	public Mono<ResourceEntity> save(ResourceEntity entity) {
		return SessionContext.userId()
				.flatMap(currentUserId -> {
					entity.setCreatedBy(currentUserId);
					return template.insert(ResourceEntity.class).using(entity);
				});
	}

	public Mono<Long> modify(Resource resource) {
		return SessionContext.userId()
				.flatMap(currentUserId -> template.update(
						Query.query(Criteria.where("id").is(resource.getId())),
						Update.update("name", resource.getName())
								.set("resource", resource.getResource())
								.set("page", resource.getPageId())
								.set("service", resource.getServiceId())
								.set("type", resource.getType())
								.set("status", resource.getStatus())
								.set("sort", resource.getSort())
								.set("modified_by", currentUserId),
						ResourceEntity.class));
	}

	public Mono<Long> deleteById(long id) {
		return template.delete(ResourceEntity.class).matching(Query.query(where("id").is(id))).all();
	}

	public Mono<ResourceEntity> findById(long id) {
		return template.selectOne(Query.query(where("id").is(id)), ResourceEntity.class);
	}

	public Flux<ResourceEntity> find(Resource resource) {
		List<Criteria> list = new ArrayList<>();
		if (resource.getServiceId() != null) {
			list.add(where("service_id").is(resource.getServiceId()));
		}
		if (resource.getPageId() != null) {
			list.add(where("page_id").is(resource.getPageId()));
		}
		if (StringUtils.isNotEmpty(resource.getResource())) {
			list.add(where("resource").like(resource.getResource()));
		}
		if (StringUtils.isNotEmpty(resource.getType())) {
			list.add(where("type").is(resource.getType()));
		}
		if (StringUtils.isNotEmpty(resource.getStatus())) {
			list.add(where("status").is(resource.getStatus()));
		}
		return template.select(ResourceEntity.class)
				.matching(Query.query(Criteria.from(list))).all();
	}

}
