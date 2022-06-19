package queue;

import java.util.function.Predicate;

/*
Model:
Let immutable(b):
    b'.size == b.size && for all i in 0..b.length-1 b'[i] = b[i]
Invariant:
    n >= 0; b[0], ..., b[n - 1] && (for all i in 0..n - 1: b[i] != null)
 */
public interface Queue {
    /*
    Pred: element != null
    Post: n' = n + 1 && for all i in 0..n - 1 b'[i] = b[i] && b'[n] = element
     */
    void enqueue(Object element);

    /*
    Pred: n >= 1
    Post: n' = n - 1 && for all i in 0 ... n - 2 b[i]' = b[i + 1] && R = b[0]
     */
    Object dequeue();

    /*
    Pred: n >= 1
    Post: R = b[0] && immutable(b)
     */
    Object element();

    /*
    Pred: true
    Post: n = 0
     */
    void clear();

    /*
    Pred: true
    Post: R = n == 0 && immutable(b)
     */
    boolean isEmpty();

    /*
    Pred: true
    Post: R = n && immutable(b)
     */
    int size();

    /*
    Pred: predicate != null
    Post:
    ( indexes = [i | i in 0..b.length-1 && predicate.test(b[i])] ) &&
    ( n'=|indexes|                                               ) &&
    ( for all j in 0..n'-2: indexes[j] < indexes[j + 1]          ) &&
    ( b'.length=n'                                               ) &&
    ( for all j in 0..n'-1: b'[j]=b[indexes[j]]                  )
     */
    void retainIf(Predicate<Object> predicate);

    /*
    Pred: predicate != null
    Post:
    ( indexes = [i | i in 0..b.length-1 && !predicate.test(b[i])] ) &&
    ( n'=|indexes|                                                ) &&
    ( for all j in 0..n'-2: indexes[j] < indexes[j + 1]           ) &&
    ( b'.length=n'                                                ) &&
    ( for all j in 0..n'-1: b'[j]=b[indexes[j]]                   )
     */
    void removeIf(Predicate<Object> predicate);

    /*
    Pred: predicate != null
    Post:
    ( for all i in 0..border: predicate.test(b[i])=false     ) &&
    ( border+1>=n || predicate.test(b[border+1])=true        ) &&
    ( for all i in border+1..b.length-1: b'[i-border-1]=b[i] ) &&
    ( n'=b.length-border-1                                   )
     */
    void dropWhile(Predicate<Object> predicate);

    /*
    Pred: predicate != null
    Post:
    ( for all i in 0..border: predicate.test(b[i])=true ) &&
    ( border+1>=n || predicate.test(b[border+1])=false  ) &&
    ( for all i in 0..border: b'[i]=b[i]                ) &&
    ( n'=border                                         )
     */
    void takeWhile(Predicate<Object> predicate);
}
