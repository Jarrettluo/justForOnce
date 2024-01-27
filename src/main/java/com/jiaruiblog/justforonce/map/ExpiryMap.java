package com.jiaruiblog.justforonce.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ExpiryMap<K,V> implements Map<K,V> {

    DelayQueue<DelayedElement<K>> queue;

    HashMap<K,V> map;

    public ExpiryMap() {
        queue = new DelayQueue<>();
        map = new HashMap<>();
    }

    @Override
    public int size() {
        removeExpire();
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        removeExpire();
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        removeExpire();
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        removeExpire();
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        removeExpire();
        return map.get(key);
    }

    @Override
    public V put(K key, V value) {
        return map.put(key,value);
    }

    public V put(K key,V value,long expire,TimeUnit unit){
        DelayedElement<K> delayedElement = new DelayedElement<>(key,
                System.currentTimeMillis() + unit.toMillis(expire));
        if(queue.contains(delayedElement)){
            queue.remove(delayedElement);
        }
        queue.add(delayedElement);
        return map.put(key,value);
    }

    @Override
    public V remove(Object key) {
        DelayedElement<K> delayedElementKey = queue.stream()
                .filter(delayedElement -> delayedElement.getKey().equals(key))
                .findFirst().orElse(null);
        if(delayedElementKey !=null) {
            queue.remove(delayedElementKey);
        }
        return map.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        queue.clear();
        map.clear();
    }

    @Override
    public Set<K> keySet() {
        removeExpire();
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        removeExpire();
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        removeExpire();
        return map.entrySet();
    }

    private void removeExpire(){
        DelayedElement delayedElement = null;
        while((delayedElement = queue.poll()) != null){
            map.remove(delayedElement.getKey());
        }
    }

    private class DelayedElement<K> implements Delayed {

        private K key;
        private long expireTime;

        public DelayedElement(K key, long expireTime) {
            this.key = key;
            this.expireTime = expireTime;
        }

        public K getKey() {
            return key;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(expireTime - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            if(o == null) {
                return 1;
            }
            long otherDelay = o.getDelay(TimeUnit.MILLISECONDS);
            long thisDelay = this.getDelay(TimeUnit.MILLISECONDS);
            return Long.compare(thisDelay, otherDelay);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            DelayedElement<?> delayedElement = (DelayedElement<?>) o;
            return key.equals(delayedElement.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }
}

