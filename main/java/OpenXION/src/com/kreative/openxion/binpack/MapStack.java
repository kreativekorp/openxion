/*
 * Copyright &copy; 2010 Rebecca G. Bettencourt / Kreative Software
 * <p>
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * <a href="http://www.mozilla.org/MPL/">http://www.mozilla.org/MPL/</a>
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Lesser General Public License (the "LGPL License"), in which
 * case the provisions of LGPL License are applicable instead of those
 * above. If you wish to allow use of your version of this file only
 * under the terms of the LGPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the LGPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the LGPL License.
 * @since OpenXION 1.2
 * @author Rebecca G. Bettencourt, Kreative Software
 */

package com.kreative.openxion.binpack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

public class MapStack<K,V> implements Map<K,V> {
	private List<Map<K,V>> stack = new Vector<Map<K,V>>();
	
	public void popAll() {
		stack.clear();
	}
	
	public Map<K,V> pop() {
		return stack.isEmpty() ? null : stack.remove(0);
	}
	
	public Map<K,V> peek() {
		return stack.isEmpty() ? null : stack.get(0);
	}
	
	public void push(Map<K,V> map) {
		stack.add(0, map);
	}
	
	public int depth() {
		return stack.size();
	}

	public int size() {
		Set<K> tmp = new HashSet<K>();
		for (Map<K,V> map : stack) {
			tmp.addAll(map.keySet());
		}
		return tmp.size();
	}

	public boolean isEmpty() {
		for (Map<K,V> map : stack) {
			if (!map.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public boolean containsKey(Object key) {
		for (Map<K,V> map : stack) {
			if (map.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public boolean containsValue(Object value) {
		for (Map<K,V> map : stack) {
			if (map.containsValue(value)) {
				return true;
			}
		}
		return false;
	}

	public V get(Object key) {
		for (Map<K,V> map : stack) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		return null;
	}

	public V put(K key, V value) {
		return stack.get(0).put(key, value);
	}

	public V remove(Object key) {
		for (Map<K,V> map : stack) {
			if (map.containsKey(key)) {
				return map.remove(key);
			}
		}
		return null;
	}

	public void putAll(Map<? extends K, ? extends V> t) {
		stack.get(0).putAll(t);
	}

	public void clear() {
		for (Map<K,V> map : stack) {
			map.clear();
		}
	}

	public Set<K> keySet() {
		return new Set<K>() {
			public int size() {
				return MapStack.this.size();
			}
			public boolean isEmpty() {
				return MapStack.this.isEmpty();
			}
			public boolean contains(Object o) {
				return MapStack.this.containsKey(o);
			}
			public Iterator<K> iterator() {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				final Iterator<K> tmpi = tmp.iterator();
				return new Iterator<K>() {
					K last = null;
					public boolean hasNext() {
						return tmpi.hasNext();
					}
					public K next() {
						return last = tmpi.next();
					}
					public void remove() {
						MapStack.this.remove(last);
					}
				};
			}
			public Object[] toArray() {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				return tmp.toArray();
			}
			public <T> T[] toArray(T[] a) {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				return tmp.toArray(a);
			}
			public boolean add(K o) {
				throw new UnsupportedOperationException();
			}
			public boolean remove(Object o) {
				for (Map<K,V> map : stack) {
					if (map.containsKey(o)) {
						map.remove(o);
						return true;
					}
				}
				return false;
			}
			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (!MapStack.this.containsKey(o)) {
						return false;
					}
				}
				return true;
			}
			public boolean addAll(Collection<? extends K> c) {
				throw new UnsupportedOperationException();
			}
			public boolean retainAll(Collection<?> c) {
				boolean ret = false;
				Set<K> tmp = new HashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				tmp.removeAll(c);
				for (Map<K,V> map : stack) {
					for (K key : tmp) {
						if (map.containsKey(key)) {
							map.remove(key);
							ret = true;
						}
					}
				}
				return ret;
			}
			public boolean removeAll(Collection<?> c) {
				boolean ret = false;
				for (Object o : c) {
					for (Map<K,V> map : stack) {
						if (map.containsKey(o)) {
							map.remove(o);
							ret = true;
							break;
						}
					}
				}
				return ret;
			}
			public void clear() {
				MapStack.this.clear();
			}
		};
	}

	public Collection<V> values() {
		return new Collection<V>() {
			public int size() {
				return MapStack.this.size();
			}
			public boolean isEmpty() {
				return MapStack.this.isEmpty();
			}
			public boolean contains(Object o) {
				return MapStack.this.containsValue(o);
			}
			public Iterator<V> iterator() {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				final Iterator<K> tmpi = tmp.iterator();
				return new Iterator<V>() {
					K last = null;
					public boolean hasNext() {
						return tmpi.hasNext();
					}
					public V next() {
						return MapStack.this.get(last = tmpi.next());
					}
					public void remove() {
						MapStack.this.remove(last);
					}
				};
			}
			public Object[] toArray() {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				List<V> tmp2 = new ArrayList<V>();
				for (K key : tmp) {
					tmp2.add(MapStack.this.get(key));
				}
				return tmp2.toArray();
			}
			public <T> T[] toArray(T[] a) {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				List<V> tmp2 = new ArrayList<V>();
				for (K key : tmp) {
					tmp2.add(MapStack.this.get(key));
				}
				return tmp2.toArray(a);
			}
			public boolean add(V o) {
				throw new UnsupportedOperationException();
			}
			public boolean remove(Object o) {
				throw new UnsupportedOperationException();
			}
			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (!MapStack.this.containsValue(o)) {
						return false;
					}
				}
				return true;
			}
			public boolean addAll(Collection<? extends V> c) {
				throw new UnsupportedOperationException();
			}
			public boolean removeAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}
			public boolean retainAll(Collection<?> c) {
				throw new UnsupportedOperationException();
			}
			public void clear() {
				MapStack.this.clear();
			}
		};
	}

	public Set<Map.Entry<K,V>> entrySet() {
		return new Set<Map.Entry<K,V>>() {
			public int size() {
				return MapStack.this.size();
			}
			public boolean isEmpty() {
				return MapStack.this.isEmpty();
			}
			public boolean contains(Object o) {
				if (o instanceof Map.Entry) {
					Object key = ((Map.Entry<?,?>)o).getKey();
					Object value = ((Map.Entry<?,?>)o).getValue();
					for (Map<K,V> map : stack) {
						if (map.containsKey(key) && map.get(key).equals(value)) {
							return true;
						}
					}
				}
				return false;
			}
			public Iterator<Map.Entry<K,V>> iterator() {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				final Iterator<K> tmpi = tmp.iterator();
				return new Iterator<Map.Entry<K,V>>() {
					K last = null;
					public boolean hasNext() {
						return tmpi.hasNext();
					}
					public Map.Entry<K,V> next() {
						final K key = last = tmpi.next();
						return new Map.Entry<K,V>() {
							public K getKey() {
								return key;
							}
							public V getValue() {
								return MapStack.this.get(key);
							}
							public V setValue(V value) {
								V ret = MapStack.this.get(key);
								MapStack.this.put(key, value);
								return ret;
							}
							public boolean equals(Object o) {
								if (o instanceof Map.Entry) {
									Map.Entry<?,?> other = (Map.Entry<?,?>)o;
									return (this.getKey().equals(other.getKey()) && this.getValue().equals(other.getValue()));
								} else {
									return false;
								}
							}
							public int hashCode() {
								return this.getKey().hashCode() ^ this.getValue().hashCode();
							}
						};
					}
					public void remove() {
						MapStack.this.remove(last);
					}
				};
			}
			public Object[] toArray() {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				List<Map.Entry<K,V>> tmp2 = new ArrayList<Map.Entry<K,V>>();
				for (final K key : tmp) {
					tmp2.add(new Map.Entry<K,V>(){
						public K getKey() {
							return key;
						}
						public V getValue() {
							return MapStack.this.get(key);
						}
						public V setValue(V value) {
							V ret = MapStack.this.get(key);
							MapStack.this.put(key, value);
							return ret;
						}
						public boolean equals(Object o) {
							if (o instanceof Map.Entry) {
								Map.Entry<?,?> other = (Map.Entry<?,?>)o;
								return (this.getKey().equals(other.getKey()) && this.getValue().equals(other.getValue()));
							} else {
								return false;
							}
						}
						public int hashCode() {
							return this.getKey().hashCode() ^ this.getValue().hashCode();
						}
					});
				}
				return tmp2.toArray();
			}
			public <T> T[] toArray(T[] a) {
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				List<Map.Entry<K,V>> tmp2 = new ArrayList<Map.Entry<K,V>>();
				for (final K key : tmp) {
					tmp2.add(new Map.Entry<K,V>(){
						public K getKey() {
							return key;
						}
						public V getValue() {
							return MapStack.this.get(key);
						}
						public V setValue(V value) {
							V ret = MapStack.this.get(key);
							MapStack.this.put(key, value);
							return ret;
						}
						public boolean equals(Object o) {
							if (o instanceof Map.Entry) {
								Map.Entry<?,?> other = (Map.Entry<?,?>)o;
								return (this.getKey().equals(other.getKey()) && this.getValue().equals(other.getValue()));
							} else {
								return false;
							}
						}
						public int hashCode() {
							return this.getKey().hashCode() ^ this.getValue().hashCode();
						}
					});
				}
				return tmp2.toArray(a);
			}
			public boolean add(Map.Entry<K,V> o) {
				MapStack.this.put(o.getKey(), o.getValue());
				return true;
			}
			public boolean remove(Object o) {
				if (o instanceof Map.Entry) {
					Object key = ((Map.Entry<?,?>)o).getKey();
					Object value = ((Map.Entry<?,?>)o).getValue();
					for (Map<K,V> map : stack) {
						if (map.containsKey(key) && map.get(key).equals(value)) {
							map.remove(key);
							return true;
						}
					}
				}
				return false;
			}
			public boolean containsAll(Collection<?> c) {
				for (Object o : c) {
					if (o instanceof Map.Entry) {
						boolean ret = false;
						Object key = ((Map.Entry<?,?>)o).getKey();
						Object value = ((Map.Entry<?,?>)o).getValue();
						for (Map<K,V> map : stack) {
							if (map.containsKey(key) && map.get(key).equals(value)) {
								ret = true;
								break;
							}
						}
						if (!ret) return false;
					} else {
						return false;
					}
				}
				return true;
			}
			public boolean addAll(Collection<? extends Map.Entry<K,V>> c) {
				for (Map.Entry<K,V> o : c) {
					MapStack.this.put(o.getKey(), o.getValue());
				}
				return true;
			}
			public boolean retainAll(Collection<?> c) {
				boolean ret = false;
				LinkedHashSet<K> tmp = new LinkedHashSet<K>();
				for (Map<K,V> map : stack) {
					tmp.addAll(map.keySet());
				}
				List<Map.Entry<K,V>> tmp2 = new ArrayList<Map.Entry<K,V>>();
				for (final K key : tmp) {
					tmp2.add(new Map.Entry<K,V>(){
						public K getKey() {
							return key;
						}
						public V getValue() {
							return MapStack.this.get(key);
						}
						public V setValue(V value) {
							V ret = MapStack.this.get(key);
							MapStack.this.put(key, value);
							return ret;
						}
						public boolean equals(Object o) {
							if (o instanceof Map.Entry) {
								Map.Entry<?,?> other = (Map.Entry<?,?>)o;
								return (this.getKey().equals(other.getKey()) && this.getValue().equals(other.getValue()));
							} else {
								return false;
							}
						}
						public int hashCode() {
							return this.getKey().hashCode() ^ this.getValue().hashCode();
						}
					});
				}
				tmp2.removeAll(c);
				for (Map<K,V> map : stack) {
					for (Map.Entry<K,V> e : tmp2) {
						if (map.containsKey(e.getKey()) && map.get(e.getKey()).equals(e.getValue())) {
							map.remove(e.getKey());
							ret = true;
						}
					}
				}
				return ret;
			}
			public boolean removeAll(Collection<?> c) {
				boolean ret = false;
				for (Object o : c) {
					if (o instanceof Map.Entry) {
						Object key = ((Map.Entry<?,?>)o).getKey();
						Object value = ((Map.Entry<?,?>)o).getValue();
						for (Map<K,V> map : stack) {
							if (map.containsKey(key) && map.get(key).equals(value)) {
								map.remove(key);
								ret = true;
								break;
							}
						}
					}
				}
				return ret;
			}
			public void clear() {
				MapStack.this.clear();
			}
		};
	}
}
