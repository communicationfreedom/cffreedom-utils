package com.cffreedom.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Original Class: com.cffreedom.utils.Cacher
 * @author MarkJacobsen.net
 * Copyright: Communication Freedom, LLC - http://www.communicationfreedom.com
 * 
 * Free to use, modify, redistribute.  Must keep full class header including 
 * copyright and note your modifications.
 * 
 * If this helped you out or saved you time, please consider...
 * 1) Donating: http://www.communicationfreedom.com/go/donate/
 * 2) Shoutout on twitter: @MarkJacobsen or @cffreedom
 * 3) Linking to: http://markjacobsen.net
 * 
 * Changes:
 * 2014-04-28 	MarkJacobsen.net 	Created
 * 2015-10-27   MarkJacobsen.net 	Added maintainOrder option
 * 2017-01-12 	MarkJacobsen.net 	More aggressive cleanup of expired items
 */
public class Cacher {
	public static final int DEFAULT_CACHE_MIN = 60;
	private static int defaultCacheMin = DEFAULT_CACHE_MIN;
	private boolean maintainOrder = false;
	private Map<String, CachedObject> cache = new HashMap<>();
	
	public Cacher(){}
	public Cacher(int defaultCacheMinutes) { this(defaultCacheMinutes, false); }
	public Cacher(int defaultCacheMinutes, boolean maintainOrder) { 
		defaultCacheMin = defaultCacheMinutes;
		this.maintainOrder = maintainOrder;
		if (maintainOrder == true) {
			cache = new LinkedHashMap<>();
		} else {
			cache = new HashMap<>();
		}
	}
	
	/**
	 * Put an item into the cache (with the default cache minutes)
	 * @param key
	 * @param obj
	 */
	public void put(String key, Object obj) {
		this.put(key, obj, defaultCacheMin);
	}
	
	/**
	 * Put an item into the cache (with the specified cache minutes)
	 * @param key
	 * @param obj
	 * @param cacheMinutes
	 */
	public void put(String key, Object obj, int cacheMinutes) {
		CachedObject newObj = new CachedObject(obj, cacheMinutes);
		cache.put(key, newObj);
		this.cleanupExpiredItems();
	}
	
	/**
	 * Get an object from the cache
	 * @param key
	 * @return
	 */
	public <T extends Object> T get(String key)	{
		this.cleanupExpiredItems();
		CachedObject obj = cache.get(key);
		
		if (obj == null) {
			return null;
		} else {
			return (T)obj.getObj();
		}
	}
	
	public int size() {
		this.cleanupExpiredItems();
		return cache.size();
	}
	
	/**
	 * Clear all cached items
	 */
	public void clear()	{
		if (maintainOrder) {
			cache = new LinkedHashMap<>();
		} else {
			cache = new HashMap<>();
		}
	}
	
	/**
	 * Remove a specific item from the cache
	 * @param key
	 */
	public void remove(String key) {
		cache.remove(key);
	}
	
	public Set<String> keySet() {
		return cache.keySet();
	}
	
	/**
	 * Say if the cache contains an item with the specified key
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key) {
		// Note: Using call to cache.get() instead of directly calling containsKey()
		// because the call to get() will expire the item if necessary
		if (cache.get(key) == null) {
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Cleanup (remove) any expired cached objects
	 */
	public void cleanupExpiredItems() {
		// Done as a 2 step process to eliminate concurrency issues
		List<String> removeKeys = new ArrayList<>();
		
		for (String key : cache.keySet()) {
			if (cache.get(key).isExpired() == true) {
				removeKeys.add(key);
			}
		}
		
		for (String key : removeKeys) {
			this.remove(key);
		}
	}
	
	/**
	 * Get the Calendar representation of when an item was last cached 
	 * (null if it has expired or does not exist)
	 * @param key
	 * @return
	 */
	public Calendar getCachedTime(String key) {
		if (this.containsKey(key) == true) {
			return cache.get(key).getCacheTime();
		} else {
			return null;
		}
	}
	
	/**
	 * Print out information about the cache
	 */
	public void printCache() {
		for (String key : cache.keySet()) {
			CachedObject obj = cache.get(key);
			Utils.output(key + " - cached: " + Format.date(Format.DATE_TIMESTAMP, Convert.toDate(obj.getCacheTime())) + " - Cache minutes: " + obj.getCacheMinutes());
		}
	}
	
	class CachedObject {
		private Object obj = null;
		private Calendar cached = Calendar.getInstance();
		private int cacheMinutes = 0;
		
		public CachedObject(Object obj, int cacheMinutes) {
			this.obj = obj;
			this.cached = Calendar.getInstance();
			this.cacheMinutes = cacheMinutes;
		}
		
		public Object getObj() { return this.obj; }
		public Calendar getCacheTime() { return this.cached; }
		public int getCacheMinutes() { return this.cacheMinutes; }
		
		public boolean isExpired() {
			Calendar testDate = DateTimeUtils.dateAdd(Calendar.getInstance(), this.cacheMinutes * -1, DateTimeUtils.DATE_PART_MINUTE);
			return this.cached.before(testDate);	
		}
	}
}
