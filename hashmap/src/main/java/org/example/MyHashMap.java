package org.example;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class MyHashMap<K, V> implements Map<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;
    private int threshold;
    private final float loadFactor;
    private int modCount;

    private transient Set<Map.Entry<K, V>> entrySet;
    private transient Set<K> keySet;
    private transient Collection<V> values;

    private static class Node<K, V> implements Map.Entry<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public K getKey() { return key; }

        @Override
        public V getValue() { return value; }

        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Map.Entry<?, ?> other)) {
                return false;
            }
            return Objects.equals(key, other.getKey())
                    && Objects.equals(value, other.getValue());
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }
    }

    public MyHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public MyHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive");
        }
        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Load factor must be positive");
        }

        int capacity = tableSizeFor(initialCapacity);
        this.loadFactor = loadFactor;
        this.table = (Node<K, V>[]) new Node[capacity];
        this.threshold = Math.max(1, (int) (capacity * loadFactor));
        this.size = 0;
        this.modCount = 0;
    }

    private int tableSizeFor(int capacity) {
        int n = 1;
        while (n < capacity) {
            n <<= 1;
        }
        return n;
    }

    private int hash(Object key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        return h ^ (h >>> 16);
    }

    private int indexFor(int hash, int length) {
        return hash & (length - 1);
    }

    private int indexFor(int hash) {
        return indexFor(hash, table.length);
    }

    private Node<K, V> findNode(Object key) {
        int hash = hash(key);
        int index = indexFor(hash);

        Node<K, V> current = table[index];
        while (current != null) {
            if (current.hash == hash && Objects.equals(current.key, key)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    private Node<K, V> getNode(Object key) {
        return findNode(key);
    }

    private void addNode(int hash, K key, V value, int index) {
        Node<K, V> newNode = new Node<>(hash, key, value, table[index]);
        table[index] = newNode;
        size++;
        modCount++;

        if (size > threshold) {
            resize();
        }
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldTable = table;
        int newCapacity = oldTable.length * 2;
        Node<K, V>[] newTable = (Node<K, V>[]) new Node[newCapacity];

        for (Node<K, V> head : oldTable) {
            Node<K, V> current = head;
            while (current != null) {
                Node<K, V> next = current.next;
                int newIndex = indexFor(current.hash, newCapacity);
                current.next = newTable[newIndex];
                newTable[newIndex] = current;

                current = next;
            }
        }
        table = newTable;
        threshold = Math.max(1, (int) (newCapacity * loadFactor));
    }

    private V removeNode(Object key) {
        int hash = hash(key);
        int index = indexFor(hash);
        Node<K, V> current = table[index];
        Node<K, V> previous = null;

        while (current != null) {
            if (current.hash == hash && Objects.equals(current.key, key)) {
                if (previous == null) {
                    table[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                modCount++;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        return null;
    }

    private boolean removeEntry(Object key, Object value) {
        int hash = hash(key);
        int index = indexFor(hash);
        Node<K, V> current = table[index];
        Node<K, V> previous = null;

        while (current != null) {
            if (current.hash == hash
                    && Objects.equals(current.key, key)
                    && Objects.equals(current.value, value)) {

                if (previous == null) {
                    table[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                modCount++;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return getNode(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (Node<K, V> head : table) {
            Node<K, V> current = head;
            while (current != null) {
                if (Objects.equals(current.value, value)) {
                    return true;
                }
                current = current.next;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        Node<K, V> node = getNode(key);
        return node == null ? null : node.value;
    }

    @Override
    public V put(K key, V value) {
        int hash = hash(key);
        int index = indexFor(hash);

        Node<K, V> current = table[index];
        while (current != null) {
            if (current.hash == hash && Objects.equals(current.key, key)) {
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            current = current.next;
        }
        addNode(hash, key, value, index);
        return null;
    }

    @Override
    public V remove(Object key) {
        return removeNode(key);
    }

    public V replace(K key, V newValue) {
        Node<K, V> node = getNode(key);
        if (node == null) {
            return null;
        }
        V oldValue = node.value;
        node.value = newValue;
        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        int capacity = table.length;
        table = (Node<K, V>[]) new Node[capacity];
        size = 0;
        threshold = Math.max(1, (int) (capacity * loadFactor));
        modCount++;
    }

    private abstract class HashIterator<T> implements Iterator<T> {
        int expectedModCount = modCount;
        int bucketIndex = 0;
        Node<K, V> next;
        Node<K, V> current;

        HashIterator() {
            advanceToNextNode();
        }

        private void advanceToNextNode() {
            while (bucketIndex < table.length && table[bucketIndex] == null) {
                bucketIndex++;
            }
            next = bucketIndex < table.length ? table[bucketIndex] : null;
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        Node<K, V> nextNode() {
            if (next == null) {
                throw new NoSuchElementException();
            }
            current = next;

            if (next.next != null) {
                next = next.next;
            } else {
                bucketIndex++;
                advanceToNextNode();
            }
            return current;
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }

            MyHashMap.this.remove(current.key);
            current = null;
            expectedModCount = modCount;
        }
    }

    private final class KeyIterator extends HashIterator<K> {
        @Override
        public K next() {
            return nextNode().key;
        }
    }

    private final class ValueIterator extends HashIterator<V> {
        @Override
        public V next() {
            return nextNode().value;
        }
    }

    private final class EntryIterator extends HashIterator<Map.Entry<K, V>> {
        @Override
        public Map.Entry<K, V> next() {
            return nextNode();
        }
    }

    private final class KeySet extends AbstractSet<K> {
        @Override
        public int size() {
            return MyHashMap.this.size;
        }

        @Override
        public void clear() {
            MyHashMap.this.clear();
        }

        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return MyHashMap.this.remove(o) != null;
        }
    }

    private final class Values extends AbstractCollection<V> {
        @Override
        public int size() {
            return MyHashMap.this.size;
        }

        @Override
        public void clear() {
            MyHashMap.this.clear();
        }

        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }

        @Override
        public boolean contains(Object o) {
            return containsValue(o);
        }
    }

    private final class EntrySet extends AbstractSet<Map.Entry<K, V>> {
        @Override
        public int size() {
            return MyHashMap.this.size;
        }

        @Override
        public void clear() {
            MyHashMap.this.clear();
        }

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry<?, ?> entry)) {
                return false;
            }

            Node<K, V> candidate = getNode(entry.getKey());
            return candidate != null && Objects.equals(candidate.getValue(), entry.getValue());
        }

        @Override
        public boolean remove(Object o) {
            if (!(o instanceof Map.Entry<?, ?> entry)) {
                return false;
            }
            return removeEntry(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> ks = keySet;
        if (ks == null) {
            ks = new KeySet();
            keySet = ks;
        }
        return ks;
    }

    @Override
    public Collection<V> values() {
        Collection<V> vs = values;
        if (vs == null) {
            vs = new Values();
            values = vs;
        }
        return vs;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K, V>> es = entrySet;
        if (es == null) {
            es = new EntrySet();
            entrySet = es;
        }
        return es;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;

        for (Node<K, V> head : table) {
            Node<K, V> current = head;
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.key).append("=").append(current.value);
                first = false;
                current = current.next;
            }
        }
        sb.append("}");
        return sb.toString();
    }
}