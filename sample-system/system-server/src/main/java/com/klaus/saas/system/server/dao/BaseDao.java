package com.klaus.saas.system.server.dao;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.data.relational.core.mapping.Column;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Klaus
 * @since 2023/7/28
 */
public class BaseDao {

	protected void mappingValues(Row row, RowMetadata rowMetadata, Class<?> cls, Object obj) {
		Field[] fields = cls.getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			if (column != null && rowMetadata.contains(column.value())) {
				String fieldName = field.getName();
				String columnValue = column.value();
				String setter = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try {
					Class<?> parameterType = field.getType();
					cls.getMethod(setter, parameterType).invoke(obj, row.get(columnValue, parameterType));
				} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
