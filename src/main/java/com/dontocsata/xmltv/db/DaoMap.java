package com.dontocsata.xmltv.db;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.j256.ormlite.dao.Dao;

public class DaoMap<K, V> extends AbstractMap<K, V> {

	private Dao<V, K> dao;

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		try {
			return dao.queryForId((K) key);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public V put(K key, V value) {
		try {
			dao.createOrUpdate(value);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public int size() {
		try {
			return (int) dao.countOf();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return new AbstractSet<Map.Entry<K, V>>() {

			@Override
			public Iterator<java.util.Map.Entry<K, V>> iterator() {
				try {
					List<V> values = dao.queryForAll();
					Iterator<V> it = values.iterator();
					return new Iterator<Map.Entry<K, V>>() {

						@Override
						public boolean hasNext() {
							return it.hasNext();
						}

						@Override
						public java.util.Map.Entry<K, V> next() {
							try {
								V value = it.next();
								K key = dao.extractId(value);
								return new SimpleEntry<K, V>(key, value);
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}

						}
					};
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public int size() {
				return DaoMap.this.size();
			}
		};
	}

}
