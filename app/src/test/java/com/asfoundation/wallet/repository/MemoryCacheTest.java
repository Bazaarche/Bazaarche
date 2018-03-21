package com.asfoundation.wallet.repository;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by trinkes on 3/15/18.
 */
public class MemoryCacheTest {
  private MemoryCache<Integer, Integer> cache;

  @Before public void before() {
    cache = new MemoryCache<Integer, Integer>(BehaviorSubject.create(), new ConcurrentHashMap());
  }

  @Test public void add() throws Exception {
    cache.save(1, 2)
        .subscribe();
    TestObserver<List<Integer>> testObserver = new TestObserver<>();
    cache.getAll()
        .subscribe(testObserver);
    assertEquals(testObserver.valueCount(), 1);
    assertEquals(testObserver.values()
        .get(0)
        .get(0), new Integer(2));
  }

  @Test public void getAll() throws Exception {
    cache.save(1, 2)
        .subscribe();
    TestObserver<List<Integer>> testObserver = new TestObserver<>();
    cache.getAll()
        .subscribe(testObserver);
    assertEquals(testObserver.valueCount(), 1);
    assertEquals(testObserver.values()
        .get(0)
        .get(0), new Integer(2));
    assertEquals(testObserver.values()
        .get(0)
        .size(), 1);

    cache.save(3, 4)
        .subscribe();
    assertEquals(testObserver.valueCount(), 2);
    assertEquals(testObserver.values()
        .get(1)
        .get(0), new Integer(2));
    assertEquals(testObserver.values()
        .get(1)
        .get(1), new Integer(4));
    assertEquals(testObserver.values()
        .get(1)
        .size(), 2);
  }

  @Test public void get() {
    cache.save(1, 2)
        .subscribe();
    TestObserver<Integer> testObserver = new TestObserver<>();
    cache.get(1)
        .subscribe(testObserver);

    assertEquals(testObserver.valueCount(), 1);
    assertEquals(testObserver.values()
        .get(0), new Integer(2));

    cache.save(1, 3)
        .subscribe();

    assertEquals(testObserver.valueCount(), 2);
    assertEquals(testObserver.values()
        .get(1), new Integer(3));
  }

  @Test public void remove() throws Exception {
    cache.save(1, 2)
        .subscribe();
    cache.remove(1)
        .subscribe();
    TestObserver<Object> testObserver = new TestObserver<>();
    cache.getAll()
        .subscribe(testObserver);

    assertEquals(testObserver.valueCount(), 1);
    ArrayList<Integer> expected = (ArrayList<Integer>) testObserver.values().<Integer>get(0);
    assertEquals(expected.size(), 0);
  }
}