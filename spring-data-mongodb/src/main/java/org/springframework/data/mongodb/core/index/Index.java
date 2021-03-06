/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core.index;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Order;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@SuppressWarnings("deprecation")
public class Index implements IndexDefinition {

	public enum Duplicates {
		RETAIN, DROP
	}

	private final Map<String, Direction> fieldSpec = new LinkedHashMap<String, Direction>();

	private String name;

	private boolean unique = false;

	private boolean dropDuplicates = false;

	private boolean sparse = false;

	public Index() {
	}

	public Index(String key, Direction direction) {
		fieldSpec.put(key, direction);
	}

	/**
	 * Creates a new {@link Indexed} on the given key and {@link Order}.
	 * 
	 * @deprecated use {@link #Index(String, Direction)} instead.
	 * @param key must not be {@literal null} or empty.
	 * @param order must not be {@literal null}.
	 */
	@Deprecated
	public Index(String key, Order order) {
		this(key, order.toDirection());
	}

	/**
	 * Adds the given field to the index.
	 * 
	 * @deprecated use {@link #on(String, Direction)} instead.
	 * @param key must not be {@literal null} or empty.
	 * @param order must not be {@literal null}.
	 * @return
	 */
	@Deprecated
	public Index on(String key, Order order) {
		return on(key, order.toDirection());
	}

	public Index on(String key, Direction direction) {
		fieldSpec.put(key, direction);
		return this;
	}

	public Index named(String name) {
		this.name = name;
		return this;
	}

	public Index unique() {
		this.unique = true;
		return this;
	}

	public Index sparse() {
		this.sparse = true;
		return this;
	}

	public Index unique(Duplicates duplicates) {
		if (duplicates == Duplicates.DROP) {
			this.dropDuplicates = true;
		}
		return unique();
	}

	public DBObject getIndexKeys() {
		DBObject dbo = new BasicDBObject();
		for (String k : fieldSpec.keySet()) {
			dbo.put(k, fieldSpec.get(k).equals(Direction.ASC) ? 1 : -1);
		}
		return dbo;
	}

	public DBObject getIndexOptions() {
		if (name == null && !unique) {
			return null;
		}
		DBObject dbo = new BasicDBObject();
		if (name != null) {
			dbo.put("name", name);
		}
		if (unique) {
			dbo.put("unique", true);
		}
		if (dropDuplicates) {
			dbo.put("dropDups", true);
		}
		if (sparse) {
			dbo.put("sparse", true);
		}
		return dbo;
	}

	@Override
	public String toString() {
		return String.format("Index: %s - Options: %s", getIndexKeys(), getIndexOptions());
	}
}
