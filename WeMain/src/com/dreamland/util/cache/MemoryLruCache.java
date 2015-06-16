package com.dreamland.util.cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoryLruCache<K, V> {
	private final LinkedHashMap<K, V> map;

	/* current size of cache */
	private int size;
	/* max size of cache */
	private int maxSize;

	private int putCount;
	private int evictionCount;
	private int hitCount;
	private int missCount;

	public MemoryLruCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize <= 0");
		}
		this.maxSize = maxSize;
		this.map = new LinkedHashMap<K, V>(0, 0.75f, true);
	}

	public final V get(K key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}

		V mapValue;
		synchronized (this) {
			mapValue = map.get(key);
			if (mapValue != null) {
				hitCount++;
				return mapValue;
			} else {
				missCount++;
				return null;
			}
		}
	}

	public final V put(K key, V value) {
		if (key == null || value == null) {
			throw new NullPointerException("key == null || value == null");
		}

		V previous;
		synchronized (this) {
			putCount++;
			size += sizeOf(key, value);
			// 如果这个key之前已经有对应的value，之前的value会被弹出
			previous = map.put(key, value);
			if (previous != null) {
				// 减去之前value的size
				size -= sizeOf(key, previous);
			}
		}

		trimToSize(maxSize);

		return previous;
	}

	public final V remove(K key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}

		V previous;
		synchronized (this) {
			previous = map.remove(key);
			if (previous != null) {
				size -= sizeOf(key, previous);
			}
		}
		return previous;
	}

	public final void clear() {
		trimToSize(-1); // -1 will evict 0-sized elements
	}

	/**
	 * 
	 * @param maxSize
	 *            -1 means evict all elements
	 */
	private void trimToSize(int maxSize) {
		while (true) {
			K key;
			V value;
			synchronized (this) {
				if (size < 0 || (map.isEmpty() && size != 0)) {
					throw new IllegalStateException(getClass().getName()
							+ ".sizeOf() is reporting inconsistent results!");
				}

				if (size <= maxSize || map.isEmpty()) {
					break;
				}

				// 找到队尾的value
				Map.Entry<K, V> toEvict = map.entrySet().iterator().next();
				key = toEvict.getKey();
				value = toEvict.getValue();
				map.remove(key);
				size -= sizeOf(key, value);
				evictionCount++;
			}
		}
	}

	/**
	 * 默认返回1，子类根据具体需求实现
	 * 
	 * @return
	 */
	protected int sizeOf(K key, V value) {
		return 1;
	}

	public synchronized final int size() {
		return size;
	}

	public synchronized final int maxSize() {
		return maxSize;
	}

	public synchronized final int hitCount() {
		return hitCount;
	}

	public synchronized final int missCount() {
		return missCount;
	}

	public synchronized final int putCount() {
		return putCount;
	}

	public synchronized final int evictionCount() {
		return evictionCount;
	}
}
