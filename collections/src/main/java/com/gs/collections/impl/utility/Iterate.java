/*
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.SortedSet;

import com.gs.collections.api.InternalIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.Function3;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.ByteFunction;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.IntObjectToIntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.LongObjectToLongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.collection.primitive.MutableBooleanCollection;
import com.gs.collections.api.collection.primitive.MutableByteCollection;
import com.gs.collections.api.collection.primitive.MutableCharCollection;
import com.gs.collections.api.collection.primitive.MutableDoubleCollection;
import com.gs.collections.api.collection.primitive.MutableFloatCollection;
import com.gs.collections.api.collection.primitive.MutableIntCollection;
import com.gs.collections.api.collection.primitive.MutableLongCollection;
import com.gs.collections.api.collection.primitive.MutableShortCollection;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.map.MutableMap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.partition.PartitionIterable;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.api.tuple.Twin;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.factory.Predicates;
import com.gs.collections.impl.block.factory.Procedures2;
import com.gs.collections.impl.block.procedure.MapCollectProcedure;
import com.gs.collections.impl.block.procedure.MaxComparatorProcedure;
import com.gs.collections.impl.block.procedure.MinComparatorProcedure;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.map.mutable.UnifiedMap;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.utility.internal.DefaultSpeciesNewStrategy;
import com.gs.collections.impl.utility.internal.IterableIterate;
import com.gs.collections.impl.utility.internal.RandomAccessListIterate;

/**
 * The Iterate utility class acts as a router to other utility classes to provide optimized iteration pattern
 * implementations based on the type of iterable.  The lowest common denominator used will normally be IterableIterate.
 * Iterate can be used when a JDK interface is the only type available to the developer, as it can
 * determine the best way to iterate based on instanceof checks.
 *
 * @since 1.0
 */
public final class Iterate
{
    private Iterate()
    {
        throw new AssertionError("Suppress default constructor for noninstantiability");
    }

    /**
     * The procedure is evaluated for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * Iterate.forEach(people, new Procedure<Person>()
     * {
     *     public void value(Person person)
     *     {
     *         LOGGER.info(person.getName());
     *     }
     * });
     * </pre>
     */
    public static <T> void forEach(Iterable<T> iterable, Procedure<? super T> procedure)
    {
        if (iterable instanceof InternalIterable)
        {
            ((InternalIterable<T>) iterable).forEach(procedure);
        }
        else if (iterable instanceof ArrayList)
        {
            ArrayListIterate.forEach((ArrayList<T>) iterable, procedure);
        }
        else if (iterable instanceof RandomAccess)
        {
            RandomAccessListIterate.forEach((List<T>) iterable, procedure);
        }
        else if (iterable != null)
        {
            IterableIterate.forEach(iterable, procedure);
        }
        else
        {
            throw new IllegalArgumentException("Cannot perform a forEach on null");
        }
    }

    /**
     * The procedure2 is evaluated for each element of the iterable with the specified parameter passed
     * as the second argument.
     * <p/>
     * <pre>e.g.
     * Iterate.forEachWith(people, new Procedure2<Person, Person>()
     * {
     *     public void value(Person person, Person other)
     *     {
     *         if (person.isRelatedTo(other))
     *         {
     *              LOGGER.info(person.getName());
     *         }
     *     }
     * }, fred);
     * </pre>
     */
    public static <T, P> void forEachWith(
            Iterable<T> iterable,
            Procedure2<? super T, ? super P> procedure,
            P parameter)
    {
        if (iterable instanceof InternalIterable)
        {
            ((InternalIterable<T>) iterable).forEachWith(procedure, parameter);
        }
        else if (iterable instanceof ArrayList)
        {
            ArrayListIterate.forEachWith((ArrayList<T>) iterable, procedure, parameter);
        }
        else if (iterable instanceof RandomAccess)
        {
            RandomAccessListIterate.forEachWith((List<T>) iterable, procedure, parameter);
        }
        else if (iterable != null)
        {
            IterableIterate.forEachWith(iterable, procedure, parameter);
        }
        else
        {
            throw new IllegalArgumentException("Cannot perform a forEachWith on null");
        }
    }

    /**
     * Iterates over a collection passing each element and the current relative int index to the specified instance of
     * ObjectIntProcedure.
     */
    public static <T> void forEachWithIndex(Iterable<T> iterable, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        if (iterable instanceof InternalIterable)
        {
            ((InternalIterable<T>) iterable).forEachWithIndex(objectIntProcedure);
        }
        else if (iterable instanceof ArrayList)
        {
            ArrayListIterate.forEachWithIndex((ArrayList<T>) iterable, objectIntProcedure);
        }
        else if (iterable instanceof RandomAccess)
        {
            RandomAccessListIterate.forEachWithIndex((List<T>) iterable, objectIntProcedure);
        }
        else if (iterable != null)
        {
            IterableIterate.forEachWithIndex(iterable, objectIntProcedure);
        }
        else
        {
            throw new IllegalArgumentException("Cannot perform a forEachWithIndex on null");
        }
    }

    /**
     * Returns a new collection with only the elements that evaluated to true for the specified predicate.
     * <p/>
     * <pre>e.g.
     * return Iterate.<b>select</b>(collection, new Predicate&lt;Person&gt;()
     * {
     *     public boolean value(Person person)
     *     {
     *         return person.getAddress().getCity().equals("Metuchen");
     *     }
     * });
     * </pre>
     */
    public static <T> Collection<T> select(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).select(predicate);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.select((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.select((List<T>) iterable, predicate);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.select(
                    iterable,
                    predicate,
                    DefaultSpeciesNewStrategy.INSTANCE.<T>speciesNew((Collection<T>) iterable));
        }
        if (iterable != null)
        {
            return IterableIterate.select(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot perform a select on null");
    }

    /**
     * Returns a new collection with only elements that evaluated to true for the specified predicate and parameter.
     * <p/>
     * <pre>e.g.
     * return Iterate.<b>selectWith</b>(integers, Predicates2.equal(), new Integer(5));
     * </pre>
     */
    public static <T, IV> Collection<T> selectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super IV> predicate,
            IV parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).selectWith(predicate, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.selectWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.selectWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.selectWith(
                    iterable,
                    predicate,
                    parameter,
                    DefaultSpeciesNewStrategy.INSTANCE.<T>speciesNew((Collection<T>) iterable));
        }
        if (iterable != null)
        {
            return IterableIterate.selectWith(iterable, predicate, parameter, FastList.<T>newList());
        }
        throw new IllegalArgumentException("Cannot perform a selectWith on null");
    }

    /**
     * Filters a collection into two separate collections based on a predicate returned via a Twin.
     * <p/>
     * <pre>e.g.
     * return Iterate.<b>selectAndRejectWith</b>(lastNames, Predicates2.lessThan(), "Mason");
     * </pre>
     */
    public static <T, IV> Twin<MutableList<T>> selectAndRejectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).selectAndRejectWith(predicate, injectedValue);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.selectAndRejectWith((ArrayList<T>) iterable, predicate, injectedValue);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.selectAndRejectWith((List<T>) iterable, predicate, injectedValue);
        }
        if (iterable != null)
        {
            return IterableIterate.selectAndRejectWith(iterable, predicate, injectedValue);
        }
        throw new IllegalArgumentException("Cannot perform a selectAndRejectWith on null");
    }

    /**
     * Filters a collection into a PartitionIterable based on a predicate.
     * <p/>
     * <pre>e.g.
     * return Iterate.<b>partition</b>(collection, new Predicate&lt;Person&gt;()
     * {
     *     public boolean value(Person person)
     *     {
     *         return person.getAddress().getState().getName().equals("New York");
     *     }
     * });
     * </pre>
     */
    public static <T> PartitionIterable<T> partition(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof RichIterable<?>)
        {
            return ((RichIterable<T>) iterable).partition(predicate);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.partition((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.partition((List<T>) iterable, predicate);
        }
        if (iterable != null)
        {
            return IterableIterate.partition(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot perform a partition on null");
    }

    /**
     * Filters a collection into a PartitionIterable based on a predicate.
     * <p/>
     * <pre>e.g.
     * return Iterate.<b>partitionWith</b>(collection, new Predicate&lt;Person, String&gt;()
     * {
     *     public boolean value(Person person, String state)
     *     {
     *         return person.getAddress().getState().getName().equals(state);
     *     }
     * }, "New York");
     * </pre>
     *
     * @since 5.0.
     */
    public static <T, P> PartitionIterable<T> partitionWith(Iterable<T> iterable, Predicate2<? super T, ? super P> predicate, P parameter)
    {
        if (iterable instanceof RichIterable<?>)
        {
            return ((RichIterable<T>) iterable).partitionWith(predicate, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.partitionWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.partitionWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable != null)
        {
            return IterableIterate.partitionWith(iterable, predicate, parameter);
        }
        throw new IllegalArgumentException("Cannot perform a partition on null");
    }

    /**
     * Returns a new collection with only the elements that are instances of the Class {@code clazz}.
     */
    public static <T> Collection<T> selectInstancesOf(Iterable<?> iterable, Class<T> clazz)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<?>) iterable).selectInstancesOf(clazz);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.selectInstancesOf((ArrayList<?>) iterable, clazz);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.selectInstancesOf((List<?>) iterable, clazz);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.selectInstancesOf(
                    iterable,
                    clazz,
                    DefaultSpeciesNewStrategy.INSTANCE.<T>speciesNew((Collection<?>) iterable));
        }
        if (iterable != null)
        {
            return IterableIterate.selectInstancesOf(iterable, clazz);
        }
        throw new IllegalArgumentException("Cannot perform a selectInstancesOf on null");
    }

    /**
     * Returns the total number of elements that evaluate to true for the specified predicate.
     * <p/>
     * <pre>e.g.
     * return Iterate.<b>count</b>(collection, new Predicate&lt;Person&gt;()
     * {
     *     public boolean value(Person person)
     *     {
     *         return person.getAddress().getState().getName().equals("New York");
     *     }
     * });
     * </pre>
     */
    public static <T> int count(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).count(predicate);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.count((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.count((List<T>) iterable, predicate);
        }
        if (iterable != null)
        {
            return IterableIterate.count(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot get a count from null");
    }

    /**
     * Returns the total number of elements that evaluate to true for the specified predicate2 and parameter.
     * <p/>
     * <pre>e.g.
     * return Iterate.<b>countWith</b>(lastNames, Predicates2.equal(), "Smith");
     * </pre>
     */
    public static <T, IV> int countWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super IV> predicate,
            IV injectedValue)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).countWith(predicate, injectedValue);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.countWith((ArrayList<T>) iterable, predicate, injectedValue);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.countWith((List<T>) iterable, predicate, injectedValue);
        }
        if (iterable != null)
        {
            return IterableIterate.countWith(iterable, predicate, injectedValue);
        }
        throw new IllegalArgumentException("Cannot get a count from null");
    }

    /**
     * @see RichIterable#collectIf(Predicate, Function)
     */
    public static <T, V> Collection<V> collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectIf(predicate, function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectIf((ArrayList<T>) iterable, predicate, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectIf((List<T>) iterable, predicate, function);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.collectIf(
                    iterable,
                    predicate,
                    function,
                    DefaultSpeciesNewStrategy.INSTANCE.<V>speciesNew((Collection<T>) iterable));
        }
        if (iterable != null)
        {
            return IterableIterate.collectIf(iterable, predicate, function);
        }
        throw new IllegalArgumentException("Cannot perform a collectIf on null");
    }

    /**
     * @see RichIterable#collectIf(Predicate, Function, Collection)
     */
    public static <T, V, R extends Collection<V>> R collectIf(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function,
            R target)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).collectIf(predicate, function, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectIf((ArrayList<T>) iterable, predicate, function, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectIf((List<T>) iterable, predicate, function, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectIf(iterable, predicate, function, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectIf on null");
    }

    /**
     * Same as the select method with two parameters but uses the specified target collection
     * <p/>
     * <pre>e.g.
     * return Iterate.select(collection, new Predicate&lt;Person&gt;()
     * {
     *     public boolean value(Person person)
     *     {
     *         return person.person.getLastName().equals("Smith");
     *     }
     * }, FastList.newList());
     * </pre>
     * <p/>
     * <pre>e.g.
     * return Iterate.select(collection, Predicates.attributeEqual("lastName", "Smith"), new ArrayList());
     * </pre>
     */
    public static <T, R extends Collection<T>> R select(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).select(predicate, targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.select((ArrayList<T>) iterable, predicate, targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.select((List<T>) iterable, predicate, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.select(iterable, predicate, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a select on null");
    }

    /**
     * Same as the selectWith method with two parameters but uses the specified target collection.
     */
    public static <T, P, R extends Collection<T>> R selectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).selectWith(predicate, parameter, targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.selectWith((ArrayList<T>) iterable, predicate, parameter, targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.selectWith((List<T>) iterable, predicate, parameter, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.selectWith(iterable, predicate, parameter, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a selectWith on null");
    }

    /**
     * Returns the first count elements of the iterable or the iterable itself if count is greater than the length of
     * the iterable.
     *
     * @param iterable the collection to take from.
     * @param count    the number of items to take.
     * @return a new list with the items take from the given collection.
     */
    public static <T> Collection<T> take(Iterable<T> iterable, int count)
    {
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.take((ArrayList<T>) iterable, count);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.take((List<T>) iterable, count);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.take(
                    iterable,
                    count,
                    DefaultSpeciesNewStrategy.INSTANCE.<T>speciesNew((Collection<T>) iterable, count));
        }
        if (iterable != null)
        {
            return IterableIterate.take(iterable, count);
        }
        throw new IllegalArgumentException("Cannot perform a take on null");
    }

    /**
     * Returns a collection without the first count elements of the iterable or the iterable itself if count is
     * non-positive.
     *
     * @param iterable the collection to drop from.
     * @param count    the number of items to drop.
     * @return a new list with the items dropped from the given collection.
     */
    public static <T> Collection<T> drop(Iterable<T> iterable, int count)
    {
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.drop((List<T>) iterable, count);
        }
        if (iterable != null)
        {
            return IterableIterate.drop(iterable, count);
        }
        throw new IllegalArgumentException("Cannot perform a drop on null");
    }

    /**
     * Returns all elements of the iterable that evaluate to false for the specified predicate.
     * <p/>
     * <pre>e.g.
     * return Iterate.reject(collection, new Predicate&lt;Person&gt;()
     * {
     *     public boolean value(Person person)
     *     {
     *         return person.person.getLastName().equals("Smith");
     *     }
     * });
     * </pre>
     * <p/>
     * <pre>e.g.
     * return Iterate.reject(collection, Predicates.attributeEqual("lastName", "Smith"));
     * </pre>
     */
    public static <T> Collection<T> reject(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).reject(predicate);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.reject((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.reject((List<T>) iterable, predicate);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.reject(
                    iterable,
                    predicate,
                    DefaultSpeciesNewStrategy.INSTANCE.<T>speciesNew((Collection<T>) iterable));
        }
        if (iterable != null)
        {
            return IterableIterate.reject(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot perform a reject on null");
    }

    /**
     * SortThis is a mutating method.  The List passed in is also returned.
     */
    public static <T extends Comparable<? super T>, L extends List<T>> L sortThis(L list)
    {
        if (list instanceof MutableList<?>)
        {
            ((MutableList<T>) list).sortThis();
        }
        else if (list instanceof ArrayList)
        {
            ArrayListIterate.sortThis((ArrayList<T>) list);
        }
        else
        {
            if (list.size() > 1)
            {
                Collections.sort(list);
            }
        }
        return list;
    }

    /**
     * SortThis is a mutating method.  The List passed in is also returned.
     */
    public static <T, L extends List<T>> L sortThis(L list, Comparator<? super T> comparator)
    {
        if (list instanceof MutableList)
        {
            ((MutableList<T>) list).sortThis(comparator);
        }
        else if (list instanceof ArrayList)
        {
            ArrayListIterate.sortThis((ArrayList<T>) list, comparator);
        }
        else
        {
            if (list.size() > 1)
            {
                Collections.sort(list, comparator);
            }
        }
        return list;
    }

    /**
     * SortThis is a mutating method.  The List passed in is also returned.
     */
    public static <T, L extends List<T>> L sortThis(L list, final Predicate2<? super T, ? super T> predicate)
    {
        return Iterate.sortThis(
                list, new Comparator<T>()
        {
            public int compare(T o1, T o2)
            {
                if (predicate.accept(o1, o2))
                {
                    return -1;
                }
                if (predicate.accept(o2, o1))
                {
                    return 1;
                }
                return 0;
            }
        });
    }

    /**
     * Sort the list by comparing an attribute defined by the function.
     * SortThisBy is a mutating method.  The List passed in is also returned.
     */
    public static <T, V extends Comparable<V>, L extends List<T>> L sortThisBy(L list, Function<? super T, ? extends V> function)
    {
        return Iterate.sortThis(list, Comparators.byFunction(function));
    }

    /**
     * Removes all elements from the iterable that evaluate to true for the specified predicate.
     */
    public static <T> Collection<T> removeIf(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof MutableCollection)
        {
            MutableCollection<T> mutableCollection = (MutableCollection<T>) iterable;
            mutableCollection.removeIf(predicate);
            return mutableCollection;
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.removeIf((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.removeIf((List<T>) iterable, predicate);
        }
        if (iterable instanceof Collection)
        {
            IterableIterate.removeIf(iterable, predicate);
            return (Collection<T>) iterable;
        }
        if (iterable != null)
        {
            IterableIterate.removeIf(iterable, predicate);
            // TODO: should this method return Iterable instead?  Would seem less useful if it did
            return null;
        }
        throw new IllegalArgumentException("Cannot perform a remove on null");
    }

    /**
     * Removes all elements of the iterable that evaluate to true for the specified predicate2 and parameter.
     */
    public static <T, P> Collection<T> removeIfWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            MutableCollection<T> mutableCollection = (MutableCollection<T>) iterable;
            mutableCollection.removeIfWith(predicate, parameter);
            return mutableCollection;
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.removeIfWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.removeIfWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof Collection)
        {
            IterableIterate.removeIfWith(iterable, predicate, parameter);
            return (Collection<T>) iterable;
        }
        if (iterable != null)
        {
            IterableIterate.removeIfWith(iterable, predicate, parameter);
            // TODO: should this method return Iterarable instead?  Would seem less useful if it did
            return null;
        }
        throw new IllegalArgumentException("Cannot perform a remove on null");
    }

    /**
     * Returns all elements of the iterable that evaluate to false for the specified predicate2 and parameter.
     */
    public static <T, P> Collection<T> rejectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).rejectWith(predicate, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.rejectWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.rejectWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.rejectWith(
                    iterable,
                    predicate,
                    parameter,
                    DefaultSpeciesNewStrategy.INSTANCE.<T>speciesNew((Collection<T>) iterable));
        }
        if (iterable != null)
        {
            return IterableIterate.rejectWith(iterable, predicate, parameter, FastList.<T>newList());
        }
        throw new IllegalArgumentException("Cannot perform a rejectWith on null");
    }

    /**
     * Same as the reject method with two parameters but uses the specified target collection for the results.
     * <p/>
     * <pre>e.g.
     * return Iterate.reject(collection, new Predicate&lt;Person&gt;()
     * {
     *     public boolean value(Person person)
     *     {
     *         return person.person.getLastName().equals("Smith");
     *     }
     * }, FastList.newList());
     * </pre>
     */
    public static <T, R extends Collection<T>> R reject(
            Iterable<T> iterable,
            Predicate<? super T> predicate,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).reject(predicate, targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.reject((ArrayList<T>) iterable, predicate, targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.reject((List<T>) iterable, predicate, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.reject(iterable, predicate, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a reject on null");
    }

    /**
     * Same as the reject method with two parameters but uses the specified target collection.
     */
    public static <T, P, R extends Collection<T>> R rejectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).rejectWith(predicate, parameter, targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.rejectWith(
                    (ArrayList<T>) iterable,
                    predicate,
                    parameter,
                    targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.rejectWith((List<T>) iterable, predicate, parameter, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.rejectWith(iterable, predicate, parameter, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a rejectWith on null");
    }

    /**
     * Add all elements from the source Iterable to the target collection, return the target collection
     */
    public static <T, R extends Collection<T>> R addAllTo(Iterable<? extends T> iterable, R targetCollection)
    {
        Iterate.addAllIterable(iterable, targetCollection);
        return targetCollection;
    }

    public static <T> boolean addAllIterable(Iterable<? extends T> iterable, Collection<T> targetCollection)
    {
        if (iterable == null)
        {
            throw new NullPointerException();
        }
        if (iterable instanceof Collection<?>)
        {
            return targetCollection.addAll((Collection<T>) iterable);
        }
        int oldSize = targetCollection.size();
        Iterate.forEachWith(iterable, Procedures2.<T>addToCollection(), targetCollection);
        return targetCollection.size() != oldSize;
    }

    /**
     * Returns a new collection with the results of applying the specified function for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collect(collection, new Function&lt;Person, String&gt;()
     * {
     *     public String value(Person person)
     *     {
     *         return person.getFirstName() + " " + person.getLastName();
     *     }
     * });
     * </pre>
     */
    public static <T, V> Collection<V> collect(
            Iterable<T> iterable,
            Function<? super T, ? extends V> function)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collect(function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collect((ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collect((List<T>) iterable, function);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.collect(
                    iterable,
                    function,
                    DefaultSpeciesNewStrategy.INSTANCE.<V>speciesNew(
                            (Collection<T>) iterable,
                            ((Collection<T>) iterable).size()));
        }
        if (iterable != null)
        {
            return IterableIterate.collect(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform a collect on null");
    }

    /**
     * Returns a new collection with the results of applying the specified booleanFunction for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectBoolean(collection, new BooleanFunction&lt;Person&gt;()
     * {
     *     public boolean booleanValueOf(Person person)
     *     {
     *         return person.canVote();
     *     }
     * });
     * </pre>
     */
    public static <T> MutableBooleanCollection collectBoolean(
            Iterable<T> iterable,
            BooleanFunction<? super T> booleanFunction)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectBoolean(booleanFunction);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectBoolean((ArrayList<T>) iterable, booleanFunction);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectBoolean((List<T>) iterable, booleanFunction);
        }
        if (iterable != null)
        {
            return IterableIterate.collectBoolean(iterable, booleanFunction);
        }
        throw new IllegalArgumentException("Cannot perform a collectBoolean on null");
    }

    /**
     * Same as {@link #collectBoolean(Iterable, BooleanFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectBoolean(collection, new BooleanFunction&lt;Person&gt;()
     * {
     *     public boolean booleanValueOf(Person person)
     *     {
     *         return person.canVote();
     *     }
     * }, new BooleanArrayList());
     * </pre>
     */
    public static <T, R extends MutableBooleanCollection> R collectBoolean(
            Iterable<T> iterable,
            BooleanFunction<? super T> booleanFunction,
            R target)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectBoolean(booleanFunction, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectBoolean((ArrayList<T>) iterable, booleanFunction, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectBoolean((List<T>) iterable, booleanFunction, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectBoolean(iterable, booleanFunction, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectBoolean on null");
    }

    /**
     * Returns a new collection with the results of applying the specified byteFunction for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectByte(collection, new ByteFunction&lt;Person&gt;()
     * {
     *     public byte byteValueOf(Person person)
     *     {
     *         return person.getCode();
     *     }
     * });
     * </pre>
     */
    public static <T> MutableByteCollection collectByte(
            Iterable<T> iterable,
            ByteFunction<? super T> byteFunction)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectByte(byteFunction);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectByte((ArrayList<T>) iterable, byteFunction);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectByte((List<T>) iterable, byteFunction);
        }
        if (iterable != null)
        {
            return IterableIterate.collectByte(iterable, byteFunction);
        }
        throw new IllegalArgumentException("Cannot perform a collectByte on null");
    }

    /**
     * Same as {@link #collectByte(Iterable, ByteFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectByte(collection, new ByteFunction&lt;Person&gt;()
     * {
     *     public byte byteValueOf(Person person)
     *     {
     *         return person.getCode();
     *     }
     * }, new ByteArrayList());
     * </pre>
     */
    public static <T, R extends MutableByteCollection> R collectByte(
            Iterable<T> iterable,
            ByteFunction<? super T> byteFunction,
            R target)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectByte(byteFunction, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectByte((ArrayList<T>) iterable, byteFunction, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectByte((List<T>) iterable, byteFunction, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectByte(iterable, byteFunction, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectByte on null");
    }

    /**
     * Returns a new collection with the results of applying the specified charFunction for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectChar(collection, new CharFunction&lt;Person&gt;()
     * {
     *     public char charValueOf(Person person)
     *     {
     *         return person.getMiddleInitial();
     *     }
     * });
     * </pre>
     */
    public static <T> MutableCharCollection collectChar(
            Iterable<T> iterable,
            CharFunction<? super T> charFunction)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectChar(charFunction);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectChar((ArrayList<T>) iterable, charFunction);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectChar((List<T>) iterable, charFunction);
        }
        if (iterable != null)
        {
            return IterableIterate.collectChar(iterable, charFunction);
        }
        throw new IllegalArgumentException("Cannot perform a collectChar on null");
    }

    /**
     * Same as {@link #collectChar(Iterable, CharFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectChar(collection, new CharFunction&lt;Person&gt;()
     * {
     *     public char charValueOf(Person person)
     *     {
     *         return person.getMiddleInitial();
     *     }
     * }, new CharArrayList());
     * </pre>
     */
    public static <T, R extends MutableCharCollection> R collectChar(
            Iterable<T> iterable,
            CharFunction<? super T> charFunction,
            R target)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectChar(charFunction, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectChar((ArrayList<T>) iterable, charFunction, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectChar((List<T>) iterable, charFunction, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectChar(iterable, charFunction, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectChar on null");
    }

    /**
     * Returns a new collection with the results of applying the specified doubleFunction for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectDouble(collection, new DoubleFunction&lt;Person&gt;()
     * {
     *     public double doubleValueOf(Person person)
     *     {
     *         return person.getMilesFromNorthPole();
     *     }
     * });
     * </pre>
     */
    public static <T> MutableDoubleCollection collectDouble(
            Iterable<T> iterable,
            DoubleFunction<? super T> doubleFunction)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectDouble(doubleFunction);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectDouble((ArrayList<T>) iterable, doubleFunction);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectDouble((List<T>) iterable, doubleFunction);
        }
        if (iterable != null)
        {
            return IterableIterate.collectDouble(iterable, doubleFunction);
        }
        throw new IllegalArgumentException("Cannot perform a collectDouble on null");
    }

    /**
     * Same as {@link #collectDouble(Iterable, DoubleFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectDouble(collection, new DoubleFunction&lt;Person&gt;()
     * {
     *     public double doubleValueOf(Person person)
     *     {
     *         return person.getMilesFromNorthPole();
     *     }
     * }, new DoubleArrayList());
     * </pre>
     */
    public static <T, R extends MutableDoubleCollection> R collectDouble(
            Iterable<T> iterable,
            DoubleFunction<? super T> doubleFunction,
            R target)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectDouble(doubleFunction, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectDouble((ArrayList<T>) iterable, doubleFunction, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectDouble((List<T>) iterable, doubleFunction, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectDouble(iterable, doubleFunction, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectDouble on null");
    }

    /**
     * Returns a new collection with the results of applying the specified floatFunction for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectFloat(collection, new FloatFunction&lt;Person&gt;()
     * {
     *     public float floatValueOf(Person person)
     *     {
     *         return person.getHeightInInches();
     *     }
     * });
     * </pre>
     */
    public static <T> MutableFloatCollection collectFloat(
            Iterable<T> iterable,
            FloatFunction<? super T> floatFunction)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectFloat(floatFunction);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectFloat((ArrayList<T>) iterable, floatFunction);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectFloat((List<T>) iterable, floatFunction);
        }
        if (iterable != null)
        {
            return IterableIterate.collectFloat(iterable, floatFunction);
        }
        throw new IllegalArgumentException("Cannot perform a collectFloat on null");
    }

    /**
     * Same as {@link #collectFloat(Iterable, FloatFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectFloat(collection, new FloatFunction&lt;Person&gt;()
     * {
     *     public float floatValueOf(Person person)
     *     {
     *         return person.getHeightInInches();
     *     }
     * }, new FloatArrayList());
     * </pre>
     */
    public static <T, R extends MutableFloatCollection> R collectFloat(
            Iterable<T> iterable,
            FloatFunction<? super T> floatFunction,
            R target)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectFloat(floatFunction, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectFloat((ArrayList<T>) iterable, floatFunction, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectFloat((List<T>) iterable, floatFunction, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectFloat(iterable, floatFunction, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectFloat on null");
    }

    /**
     * Returns a new collection with the results of applying the specified intFunction for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectInt(collection, new IntFunction&lt;Person&gt;()
     * {
     *     public int intValueOf(Person person)
     *     {
     *         return person.getAge();
     *     }
     * });
     * </pre>
     */
    public static <T> MutableIntCollection collectInt(
            Iterable<T> iterable,
            IntFunction<? super T> intFunction)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectInt(intFunction);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectInt((ArrayList<T>) iterable, intFunction);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectInt((List<T>) iterable, intFunction);
        }
        if (iterable != null)
        {
            return IterableIterate.collectInt(iterable, intFunction);
        }
        throw new IllegalArgumentException("Cannot perform a collectInt on null");
    }

    /**
     * Same as {@link #collectInt(Iterable, IntFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectInt(collection, new IntFunction&lt;Person&gt;()
     * {
     *     public int intValueOf(Person person)
     *     {
     *         return person.getAge();
     *     }
     * }, new IntArrayList());
     * </pre>
     */
    public static <T, R extends MutableIntCollection> R collectInt(
            Iterable<T> iterable,
            IntFunction<? super T> intFunction,
            R target)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectInt(intFunction, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectInt((ArrayList<T>) iterable, intFunction, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectInt((List<T>) iterable, intFunction, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectInt(iterable, intFunction, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectInt on null");
    }

    /**
     * Returns a new collection with the results of applying the specified longFunction for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectLong(collection, new LongFunction&lt;Person&gt;()
     * {
     *     public long longValueOf(Person person)
     *     {
     *         return person.getGuid();
     *     }
     * });
     * </pre>
     */
    public static <T> MutableLongCollection collectLong(
            Iterable<T> iterable,
            LongFunction<? super T> longFunction)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectLong(longFunction);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectLong((ArrayList<T>) iterable, longFunction);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectLong((List<T>) iterable, longFunction);
        }
        if (iterable != null)
        {
            return IterableIterate.collectLong(iterable, longFunction);
        }
        throw new IllegalArgumentException("Cannot perform a collectLong on null");
    }

    /**
     * Same as {@link #collectLong(Iterable, LongFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectLong(collection, new LongFunction&lt;Person&gt;()
     * {
     *     public long longValueOf(Person person)
     *     {
     *         return person.getGuid();
     *     }
     * }, new LongArrayList());
     * </pre>
     */
    public static <T, R extends MutableLongCollection> R collectLong(
            Iterable<T> iterable,
            LongFunction<? super T> longFunction,
            R target)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectLong(longFunction, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectLong((ArrayList<T>) iterable, longFunction, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectLong((List<T>) iterable, longFunction, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectLong(iterable, longFunction, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectLong on null");
    }

    /**
     * Returns a new collection with the results of applying the specified shortFunction for each element of the iterable.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectShort(collection, new ShortFunction&lt;Person&gt;()
     * {
     *     public short shortValueOf(Person person)
     *     {
     *         return person.getNumberOfJunkMailItemsReceivedPerMonth();
     *     }
     * });
     * </pre>
     */
    public static <T> MutableShortCollection collectShort(
            Iterable<T> iterable,
            ShortFunction<? super T> shortFunction)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectShort(shortFunction);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectShort((ArrayList<T>) iterable, shortFunction);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectShort((List<T>) iterable, shortFunction);
        }
        if (iterable != null)
        {
            return IterableIterate.collectShort(iterable, shortFunction);
        }
        throw new IllegalArgumentException("Cannot perform a collectShort on null");
    }

    /**
     * Same as {@link #collectShort(Iterable, ShortFunction)}, except that the results are gathered into the specified {@code target}
     * collection.
     * <p/>
     * <pre>e.g.
     * return Iterate.collectShort(collection, new ShortFunction&lt;Person&gt;()
     * {
     *     public short shortValueOf(Person person)
     *     {
     *         return person.getNumberOfJunkMailItemsReceivedPerMonth();
     *     }
     * }, new ShortArrayList());
     * </pre>
     */
    public static <T, R extends MutableShortCollection> R collectShort(
            Iterable<T> iterable,
            ShortFunction<? super T> shortFunction,
            R target)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectShort(shortFunction, target);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectShort((ArrayList<T>) iterable, shortFunction, target);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectShort((List<T>) iterable, shortFunction, target);
        }
        if (iterable != null)
        {
            return IterableIterate.collectShort(iterable, shortFunction, target);
        }
        throw new IllegalArgumentException("Cannot perform a collectShort on null");
    }

    /**
     * @see RichIterable#flatCollect(Function)
     */
    public static <T, V> Collection<V> flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).flatCollect(function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.flatCollect((ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.flatCollect((List<T>) iterable, function);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.flatCollect(
                    iterable,
                    function,
                    DefaultSpeciesNewStrategy.INSTANCE.<V>speciesNew(
                            (Collection<T>) iterable,
                            ((Collection<T>) iterable).size()));
        }
        if (iterable != null)
        {
            return IterableIterate.flatCollect(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform a flatCollect on null");
    }

    /**
     * Same as the collect method with two parameters, except that the results are gathered into the specified
     * targetCollection
     * <p/>
     * <pre>e.g.
     * return Iterate.collect(collection, new Function&lt;Person, String&gt;()
     * {
     *     public String value(Person person)
     *     {
     *         return person.getFirstName() + " " + person.getLastName();
     *     }
     * }, FastList.newList());
     * </pre>
     */
    public static <T, A, R extends Collection<A>> R collect(
            Iterable<T> iterable,
            Function<? super T, ? extends A> function,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).collect(function, targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collect((ArrayList<T>) iterable, function, targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collect((List<T>) iterable, function, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.collect(iterable, function, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a collect on null");
    }

    /**
     * @see RichIterable#flatCollect(Function, Collection)
     */
    public static <T, A, R extends Collection<A>> R flatCollect(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<A>> function,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).flatCollect(function, targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.flatCollect((ArrayList<T>) iterable, function, targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.flatCollect((List<T>) iterable, function, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.flatCollect(iterable, function, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a flatCollect on null");
    }

    /**
     * Same as collect with a Function2 and specified parameter which is passed to the function.
     */
    public static <T, P, A> Collection<A> collectWith(
            Iterable<T> iterable,
            Function2<? super T, ? super P, ? extends A> function,
            P parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).collectWith(function, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectWith((ArrayList<T>) iterable, function, parameter);
        }
        if (iterable instanceof List<?>)
        {
            return ListIterate.collectWith((List<T>) iterable, function, parameter);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.collectWith(
                    iterable,
                    function,
                    parameter,
                    DefaultSpeciesNewStrategy.INSTANCE.<A>speciesNew(
                            (Collection<T>) iterable,
                            ((Collection<T>) iterable).size()));
        }
        if (iterable != null)
        {
            return IterableIterate.collectWith(iterable, function, parameter);
        }
        throw new IllegalArgumentException("Cannot perform a collectWith on null");
    }

    /**
     * Same as collectWith but with a targetCollection parameter to gather the results.
     */
    public static <T, P, A, R extends Collection<A>> R collectWith(
            Iterable<T> iterable,
            Function2<? super T, ? super P, ? extends A> function,
            P parameter,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).collectWith(function, parameter, targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.collectWith((ArrayList<T>) iterable, function, parameter, targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.collectWith((List<T>) iterable, function, parameter, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.collectWith(iterable, function, parameter, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a collectWith on null");
    }

    /**
     * Flattens a collection of collections into one "flat" collection.
     *
     * @param iterable A list of lists, e.g. { { 1, 2, 3 }, { 4, 5 }, { 6 } }
     * @return A flattened list, e.g. { 1, 2, 3, 4, 5, 6 }
     */
    public static <T> Collection<T> flatten(Iterable<? extends Iterable<T>> iterable)
    {
        return Iterate.flatCollect(iterable, Functions.<Iterable<T>>getPassThru());
    }

    /**
     * Same as {@link #flatten(Iterable)} except that the results are gathered into the specified targetCollection.
     */
    public static <T, R extends Collection<T>> R flatten(Iterable<? extends Iterable<T>> iterable, R targetCollection)
    {
        return Iterate.flatCollect(iterable, Functions.<Iterable<T>>getPassThru(), targetCollection);
    }

    /**
     * Returns the first element of a collection.  In the case of a List it is the element at the first index.  In the
     * case of any other Collection, it is the first element that would be returned during an iteration. If the
     * Collection is empty, the result is {@code null}.
     * <p/>
     * WARNING!!! The order of Sets are not guaranteed (except for TreeSets and other Ordered Set implementations), so
     * if you use this method, the first element could be any element from the Set.
     *
     * @throws IllegalArgumentException if the Collection is null
     */
    public static <T> T getFirst(Iterable<T> iterable)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).getFirst();
        }
        if (iterable instanceof List)
        {
            return ListIterate.getFirst((List<T>) iterable);
        }
        if (iterable instanceof SortedSet && !((SortedSet<T>) iterable).isEmpty())
        {
            return ((SortedSet<T>) iterable).first();
        }
        if (iterable instanceof Collection)
        {
            return Iterate.isEmpty(iterable) ? null : iterable.iterator().next();
        }
        if (iterable != null)
        {
            return IterableIterate.getFirst(iterable);
        }
        throw new IllegalArgumentException("Cannot get first from null");
    }

    /**
     * A null-safe check on a collection to see if it isEmpty.  A null collection results in a true.
     */
    public static boolean isEmpty(Iterable<?> iterable)
    {
        if (iterable == null)
        {
            return true;
        }
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<?>) iterable).isEmpty();
        }
        if (iterable instanceof Collection)
        {
            return ((Collection<?>) iterable).isEmpty();
        }
        return IterableIterate.isEmpty(iterable);
    }

    /**
     * A null-safe check on a collection to see if it is notEmpty.  A null collection results in a false.
     */
    public static boolean notEmpty(Iterable<?> iterable)
    {
        return !Iterate.isEmpty(iterable);
    }

    /**
     * Returns the last element of a collection.  In the case of a List it is the element at the last index.  In the
     * case of any other Collection, it is the last element that would be returned during an iteration. If the
     * Collection is empty, the result is {@code null}.
     * <p/>
     * WARNING!!! The order of Sets are not guaranteed (except for TreeSets and other Ordered Set implementations), so
     * if you use this method, the last element could be any element from the Set.
     *
     * @throws IllegalArgumentException if the Collection is null
     */
    public static <T> T getLast(Iterable<T> iterable)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).getLast();
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.getLast((List<T>) iterable);
        }
        if (iterable instanceof SortedSet && !((SortedSet<T>) iterable).isEmpty())
        {
            return ((SortedSet<T>) iterable).last();
        }
        if (iterable instanceof LinkedList && !((LinkedList<T>) iterable).isEmpty())
        {
            return ((LinkedList<T>) iterable).getLast();
        }
        if (iterable != null)
        {
            return IterableIterate.getLast(iterable);
        }
        throw new IllegalArgumentException("Cannot get last from null");
    }

    /**
     * Returns the first element of the iterable that evaluates to true for the specified predicate, or null if
     * no element evaluates to true.
     * <p/>
     * <pre>e.g.
     * return Iterate.detect(collection, new Predicate&lt;Person&gt;()
     * {
     *     public boolean value(Person person)
     *     {
     *         return person.getFirstName().equals("John") && person.getLastName().equals("Smith");
     *     }
     * });
     * </pre>
     */
    public static <T> T detect(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).detect(predicate);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.detect((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.detect((List<T>) iterable, predicate);
        }
        if (iterable != null)
        {
            return IterableIterate.detect(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot perform detect on null");
    }

    /**
     * Returns the first element of the iterable that evaluates to true for the specified predicate2 and parameter,
     * or null if no element evaluates to true.
     * <p/>
     * <pre>e.g.
     * Iterate.detectWith(collection, new Predicate2&lt;Person, String&gt;()
     * {
     *     public boolean value(Person person, String fullName)
     *     {
     *         return person.getFullName().equals(fullName);
     *     }
     * }, "John Smith");
     * </pre>
     */
    public static <T, P> T detectWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).detectWith(predicate, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.detectWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.detectWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable != null)
        {
            return IterableIterate.detectWith(iterable, predicate, parameter);
        }
        throw new IllegalArgumentException("Cannot perform detectWith on null");
    }

    /**
     * Returns the first element of the iterable that evaluates to true for the specified predicate, or returns the
     * result ifNone if no element evaluates to true.
     */
    public static <T> T detectIfNone(Iterable<T> iterable, Predicate<? super T> predicate, T ifNone)
    {
        T result = Iterate.detect(iterable, predicate);
        return result == null ? ifNone : result;
    }

    /**
     * Returns the first element of the iterable that evaluates to true for the specified predicate2 and parameter,
     * or returns the result ifNone if no element evaluates to true.
     */
    public static <T, P> T detectWithIfNone(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            T ifNone)
    {
        T result = Iterate.detectWith(iterable, predicate, parameter);
        return result == null ? ifNone : result;
    }

    /**
     * Searches for the first occurrence where the predicate evaluates to true.
     */
    public static <T> int detectIndex(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof ArrayList<?>)
        {
            return ArrayListIterate.detectIndex((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof List<?>)
        {
            return ListIterate.detectIndex((List<T>) iterable, predicate);
        }
        if (iterable != null)
        {
            return IterableIterate.detectIndex(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot perform detectIndex on null");
    }

    /**
     * Searches for the first occurrence where the predicate evaluates to true.
     */
    public static <T, P> int detectIndexWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (iterable instanceof ArrayList<?>)
        {
            return ArrayListIterate.detectIndexWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof List<?>)
        {
            return ListIterate.detectIndexWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable != null)
        {
            return IterableIterate.detectIndexWith(iterable, predicate, parameter);
        }
        throw new IllegalArgumentException("Cannot perform detectIndexWith on null");
    }

    /**
     * @see RichIterable#injectInto(Object, Function2)
     */
    public static <T, IV> IV injectInto(
            IV injectValue,
            Iterable<T> iterable,
            Function2<? super IV, ? super T, ? extends IV> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).injectInto(injectValue, function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.injectInto(injectValue, (ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, (List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.injectInto(injectValue, iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    /**
     * @see RichIterable#injectInto(int, IntObjectToIntFunction)
     */
    public static <T> int injectInto(
            int injectValue,
            Iterable<T> iterable,
            IntObjectToIntFunction<? super T> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).injectInto(injectValue, function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.injectInto(injectValue, (ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, (List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.injectInto(injectValue, iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    /**
     * @see RichIterable#injectInto(long, LongObjectToLongFunction)
     */
    public static <T> long injectInto(
            long injectValue,
            Iterable<T> iterable,
            LongObjectToLongFunction<? super T> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).injectInto(injectValue, function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.injectInto(injectValue, (ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, (List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.injectInto(injectValue, iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    /**
     * @see RichIterable#injectInto(double, DoubleObjectToDoubleFunction)
     */
    public static <T> double injectInto(
            double injectValue,
            Iterable<T> iterable,
            DoubleObjectToDoubleFunction<? super T> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).injectInto(injectValue, function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.injectInto(injectValue, (ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, (List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.injectInto(injectValue, iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    /**
     * @see RichIterable#injectInto(float, FloatObjectToFloatFunction)
     */
    public static <T> float injectInto(
            float injectValue,
            Iterable<T> iterable,
            FloatObjectToFloatFunction<? super T> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).injectInto(injectValue, function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.injectInto(injectValue, (ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectInto(injectValue, (List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.injectInto(injectValue, iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    /**
     * @see RichIterable#sumOfInt(IntFunction)
     */
    public static <T> long sumOfInt(Iterable<T> iterable, IntFunction<? super T> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).sumOfInt(function);
        }
        if (iterable instanceof List)
        {
            return ListIterate.sumOfInt((List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.sumOfInt(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    /**
     * @see RichIterable#sumOfLong(LongFunction)
     */
    public static <T> long sumOfLong(Iterable<T> iterable, LongFunction<? super T> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).sumOfLong(function);
        }
        if (iterable instanceof List)
        {
            return ListIterate.sumOfLong((List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.sumOfLong(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    /**
     * @see RichIterable#sumOfFloat(FloatFunction)
     */
    public static <T> double sumOfFloat(Iterable<T> iterable, FloatFunction<? super T> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).sumOfFloat(function);
        }
        if (iterable instanceof List)
        {
            return ListIterate.sumOfFloat((List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.sumOfFloat(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    /**
     * @see RichIterable#sumOfDouble(DoubleFunction)
     */
    public static <T> double sumOfDouble(Iterable<T> iterable, DoubleFunction<? super T> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).sumOfDouble(function);
        }
        if (iterable instanceof List)
        {
            return ListIterate.sumOfDouble((List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.sumOfDouble(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform an injectInto on null");
    }

    public static <T, IV, P> IV injectIntoWith(
            IV injectValue,
            Iterable<T> iterable,
            Function3<? super IV, ? super T, ? super P, ? extends IV> function,
            P parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).injectIntoWith(injectValue, function, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.injectIntoWith(injectValue, (ArrayList<T>) iterable, function, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.injectIntoWith(injectValue, (List<T>) iterable, function, parameter);
        }
        if (iterable != null)
        {
            return IterableIterate.injectIntoWith(injectValue, iterable, function, parameter);
        }
        throw new IllegalArgumentException("Cannot perform an injectIntoWith on null");
    }

    /**
     * Returns true if the predicate evaluates to true for any element of the iterable.
     * Returns false if the iterable is empty or if no elements return true for the predicate.
     */
    public static <T> boolean anySatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).anySatisfy(predicate);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.anySatisfy((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.anySatisfy((List<T>) iterable, predicate);
        }
        if (iterable != null)
        {
            return IterableIterate.anySatisfy(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot perform an anySatisfy on null");
    }

    /**
     * Returns true if the predicate2 and parameter evaluates to true for any element of the iterable.
     * Returns false if the iterable is empty or if no elements return true for the predicate2.
     */
    public static <T, P> boolean anySatisfyWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).anySatisfyWith(predicate, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.anySatisfyWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.anySatisfyWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable != null)
        {
            return IterableIterate.anySatisfyWith(iterable, predicate, parameter);
        }
        throw new IllegalArgumentException("Cannot perform an anySatisfyWith on null");
    }

    /**
     * Returns true if the predicate evaluates to true for every element of the iterable, or returns false.
     * Returns true if the iterable is empty.
     */
    public static <T> boolean allSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).allSatisfy(predicate);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.allSatisfy((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.allSatisfy((List<T>) iterable, predicate);
        }
        if (iterable != null)
        {
            return IterableIterate.allSatisfy(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot perform an allSatisfy on null");
    }

    /**
     * Returns true if the predicate evaluates to true for every element of the iterable, or returns false.
     */
    public static <T, P> boolean allSatisfyWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).allSatisfyWith(predicate, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.allSatisfyWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.allSatisfyWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable != null)
        {
            return IterableIterate.allSatisfyWith(iterable, predicate, parameter);
        }
        throw new IllegalArgumentException("Cannot perform an allSatisfyWith on null");
    }

    /**
     * Returns true if the predicate evaluates to false for every element of the iterable, or returns false.
     * Returns true if the iterable is empty.
     */
    public static <T> boolean noneSatisfy(Iterable<T> iterable, Predicate<? super T> predicate)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).noneSatisfy(predicate);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.noneSatisfy((ArrayList<T>) iterable, predicate);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.noneSatisfy((List<T>) iterable, predicate);
        }
        if (iterable != null)
        {
            return IterableIterate.noneSatisfy(iterable, predicate);
        }
        throw new IllegalArgumentException("Cannot perform an allSatisfy on null");
    }

    /**
     * Returns true if the predicate evaluates to false for every element of the iterable, or returns false.
     * Returns true if the iterable is empty.
     */
    public static <T, P> boolean noneSatisfyWith(
            Iterable<T> iterable,
            Predicate2<? super T, ? super P> predicate,
            P parameter)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).noneSatisfyWith(predicate, parameter);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.noneSatisfyWith((ArrayList<T>) iterable, predicate, parameter);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.noneSatisfyWith((List<T>) iterable, predicate, parameter);
        }
        if (iterable != null)
        {
            return IterableIterate.noneSatisfyWith(iterable, predicate, parameter);
        }
        throw new IllegalArgumentException("Cannot perform an noneSatisfyWith on null");
    }

    /**
     * Iterate over the specified collection applying the specified Function to each element to calculate
     * a key and return the results as a Map.
     */
    public static <T, K> MutableMap<K, T> toMap(
            Iterable<T> iterable,
            Function<? super T, ? extends K> keyFunction)
    {
        MutableMap<K, T> map = UnifiedMap.newMap();
        Iterate.forEach(iterable, new MapCollectProcedure<T, K, T>(map, keyFunction));
        return map;
    }

    /**
     * Iterate over the specified collection applying the specified Functions to each element to calculate
     * a key and value, and return the results as a Map.
     */
    public static <T, K, V> MutableMap<K, V> toMap(
            Iterable<T> iterable,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction)
    {
        return Iterate.addToMap(iterable, keyFunction, valueFunction, UnifiedMap.<K, V>newMap());
    }

    /**
     * Iterate over the specified collection applying a specific Function to each element to calculate a
     * key, and return the results as a Map.
     */
    public static <T, K, V, M extends Map<K, V>> M addToMap(
            Iterable<T> iterable,
            Function<? super T, ? extends K> keyFunction,
            M map)
    {
        Iterate.forEach(iterable, new MapCollectProcedure<T, K, V>(map, keyFunction));
        return map;
    }

    /**
     * Iterate over the specified collection applying the specified Functions to each element to calculate
     * a key and value, and return the results as a Map.
     */
    public static <T, K, V, M extends Map<K, V>> M addToMap(
            Iterable<T> iterable,
            Function<? super T, ? extends K> keyFunction,
            Function<? super T, ? extends V> valueFunction,
            M map)
    {
        Iterate.forEach(iterable, new MapCollectProcedure<T, K, V>(map, keyFunction, valueFunction));
        return map;
    }

    /**
     * Return the specified collection as a sorted List.
     */
    public static <T extends Comparable<? super T>> MutableList<T> toSortedList(Iterable<T> iterable)
    {
        return Iterate.toSortedList(iterable, Comparators.naturalOrder());
    }

    /**
     * Return the specified collection as a sorted List using the specified Comparator.
     */
    public static <T> MutableList<T> toSortedList(Iterable<T> iterable, Comparator<? super T> comparator)
    {
        return FastList.newList(iterable).sortThis(comparator);
    }

    /**
     * Returns the size of an iterable.  In the case of Collections and RichIterables, the method size is called.  All
     * other iterables will force a complete iteration to happen, which can be unnecessarily costly.
     */
    public static int sizeOf(Iterable<?> iterable)
    {
        if (iterable instanceof Collection)
        {
            return ((Collection<?>) iterable).size();
        }
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<?>) iterable).size();
        }
        return Iterate.count(iterable, Predicates.alwaysTrue());
    }

    /**
     * Returns true if the iterable contains the value.  In the case of Collections and RichIterables, the method contains
     * is called.  All other iterables will force a complete iteration to happen, which can be unnecessarily costly.
     */
    public static boolean contains(Iterable<?> iterable, Object value)
    {
        if (iterable instanceof Collection)
        {
            return ((Collection<?>) iterable).contains(value);
        }
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<?>) iterable).contains(value);
        }
        return IterableIterate.detectIndex(iterable, Predicates.equal(value)) > -1;
    }

    /**
     * Converts the specified iterable to an array.
     */
    public static <T> Object[] toArray(Iterable<T> iterable)
    {
        if (iterable == null)
        {
            throw new NullPointerException();
        }
        if (iterable instanceof Collection)
        {
            return ((Collection<T>) iterable).toArray();
        }
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).toArray();
        }
        MutableList<T> result = Lists.mutable.of();
        Iterate.addAllTo(iterable, result);
        return result.toArray();
    }

    /**
     * Copies the specified iterable into the specified array.
     */
    public static <T> T[] toArray(Iterable<? extends T> iterable, T[] target)
    {
        if (iterable instanceof Collection)
        {
            return ((Collection<T>) iterable).toArray(target);
        }
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).toArray(target);
        }
        MutableList<T> result = Lists.mutable.of();
        Iterate.addAllTo(iterable, result);
        return result.toArray(target);
    }

    /**
     * @see RichIterable#groupBy(Function)
     */
    public static <T, V> MutableMultimap<V, T> groupBy(
            Iterable<T> iterable,
            Function<? super T, ? extends V> function)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).groupBy(function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.groupBy((ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.groupBy((List<T>) iterable, function);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.groupBy(iterable, function, FastListMultimap.<V, T>newMultimap());
        }
        if (iterable != null)
        {
            return IterableIterate.groupBy(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform a groupBy on null");
    }

    /**
     * @see RichIterable#groupBy(Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupBy(
            Iterable<T> iterable,
            Function<? super T, ? extends V> function,
            R targetMultimap)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).groupBy(function, targetMultimap);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.groupBy((ArrayList<T>) iterable, function, targetMultimap);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.groupBy((List<T>) iterable, function, targetMultimap);
        }
        if (iterable != null)
        {
            return IterableIterate.groupBy(iterable, function, targetMultimap);
        }
        throw new IllegalArgumentException("Cannot perform a groupBy on null");
    }

    /**
     * @see RichIterable#aggregateInPlaceBy(Function, Function0, Procedure2)
     */
    public static <T, K, V> MutableMap<K, V> aggregateInPlaceBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Procedure2<? super V, ? super T> mutatingAggregator)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).aggregateInPlaceBy(groupBy, zeroValueFactory, mutatingAggregator);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.aggregateInPlaceBy((ArrayList<T>) iterable, groupBy, zeroValueFactory, mutatingAggregator);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.aggregateInPlaceBy((List<T>) iterable, groupBy, zeroValueFactory, mutatingAggregator);
        }
        if (iterable != null)
        {
            return IterableIterate.aggregateInPlaceBy(iterable, groupBy, zeroValueFactory, mutatingAggregator);
        }
        throw new IllegalArgumentException("Cannot perform an aggregateInPlaceBy on null");
    }

    /**
     * @see RichIterable#aggregateBy(Function, Function0, Function2)
     */
    public static <T, K, V> MutableMap<K, V> aggregateBy(
            Iterable<T> iterable,
            Function<? super T, ? extends K> groupBy,
            Function0<? extends V> zeroValueFactory,
            Function2<? super V, ? super T, ? extends V> nonMutatingAggregator)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).aggregateBy(groupBy, zeroValueFactory, nonMutatingAggregator);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.aggregateBy((ArrayList<T>) iterable, groupBy, zeroValueFactory, nonMutatingAggregator);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.aggregateBy((List<T>) iterable, groupBy, zeroValueFactory, nonMutatingAggregator);
        }
        if (iterable != null)
        {
            return IterableIterate.aggregateBy(iterable, groupBy, zeroValueFactory, nonMutatingAggregator);
        }
        throw new IllegalArgumentException("Cannot perform an aggregateBy on null");
    }

    /**
     * @see RichIterable#groupByEach(Function)
     */
    public static <T, V> MutableMultimap<V, T> groupByEach(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).groupByEach(function);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.groupByEach((ArrayList<T>) iterable, function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.groupByEach((List<T>) iterable, function);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.groupByEach(iterable, function, FastListMultimap.<V, T>newMultimap());
        }
        if (iterable != null)
        {
            return IterableIterate.groupByEach(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform a groupByEach on null");
    }

    /**
     * @see RichIterable#groupByEach(Function, MutableMultimap)
     */
    public static <T, V, R extends MutableMultimap<V, T>> R groupByEach(
            Iterable<T> iterable,
            Function<? super T, ? extends Iterable<V>> function,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).groupByEach(function, targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.groupByEach((ArrayList<T>) iterable, function, targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.groupByEach((List<T>) iterable, function, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.groupByEach(iterable, function, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a groupBy on null");
    }

    /**
     * @see RichIterable#min(Comparator)
     */
    public static <T> T min(Iterable<T> iterable, Comparator<? super T> comparator)
    {
        MinComparatorProcedure<T> procedure = new MinComparatorProcedure<T>(comparator);
        Iterate.forEach(iterable, procedure);
        return procedure.getResult();
    }

    /**
     * @see RichIterable#max(Comparator)
     */
    public static <T> T max(Iterable<T> iterable, Comparator<? super T> comparator)
    {
        MaxComparatorProcedure<T> procedure = new MaxComparatorProcedure<T>(comparator);
        Iterate.forEach(iterable, procedure);
        return procedure.getResult();
    }

    /**
     * @see RichIterable#min()
     */
    public static <T> T min(Iterable<T> iterable)
    {
        return Iterate.min(iterable, Comparators.naturalOrder());
    }

    /**
     * @see RichIterable#max()
     */
    public static <T> T max(Iterable<T> iterable)
    {
        return Iterate.max(iterable, Comparators.naturalOrder());
    }

    public static <T> T getOnly(Iterable<T> iterable)
    {
        if (iterable != null)
        {
            return IterableIterate.getOnly(iterable);
        }
        throw new IllegalArgumentException("Cannot perform getOnly on null");
    }

    /**
     * @see RichIterable#zip(Iterable)
     */
    public static <X, Y> Collection<Pair<X, Y>> zip(Iterable<X> xs, Iterable<Y> ys)
    {
        if (xs instanceof MutableCollection)
        {
            return ((MutableCollection<X>) xs).zip(ys);
        }
        if (xs instanceof ArrayList)
        {
            return ArrayListIterate.zip((ArrayList<X>) xs, ys);
        }
        if (xs instanceof RandomAccess)
        {
            return RandomAccessListIterate.zip((List<X>) xs, ys);
        }
        if (xs != null)
        {
            return IterableIterate.zip(xs, ys);
        }
        throw new IllegalArgumentException("Cannot perform a zip on null");
    }

    /**
     * @see RichIterable#zip(Iterable, Collection)
     */
    public static <X, Y, R extends Collection<Pair<X, Y>>> R zip(
            Iterable<X> xs,
            Iterable<Y> ys,
            R targetCollection)
    {
        if (xs instanceof RichIterable)
        {
            return ((RichIterable<X>) xs).zip(ys, targetCollection);
        }
        if (xs instanceof ArrayList)
        {
            return ArrayListIterate.zip((ArrayList<X>) xs, ys, targetCollection);
        }
        if (xs instanceof RandomAccess)
        {
            return RandomAccessListIterate.zip((List<X>) xs, ys, targetCollection);
        }
        if (xs != null)
        {
            return IterableIterate.zip(xs, ys, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a zip on null");
    }

    /**
     * @see RichIterable#zipWithIndex()
     */
    public static <T> Collection<Pair<T, Integer>> zipWithIndex(Iterable<T> iterable)
    {
        if (iterable instanceof MutableCollection)
        {
            return ((MutableCollection<T>) iterable).zipWithIndex();
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.zipWithIndex((ArrayList<T>) iterable);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.zipWithIndex((List<T>) iterable);
        }
        if (iterable instanceof Collection)
        {
            return IterableIterate.zipWithIndex(
                    iterable,
                    DefaultSpeciesNewStrategy.INSTANCE.<Pair<T, Integer>>speciesNew(
                            (Collection<T>) iterable,
                            ((Collection<T>) iterable).size()));
        }
        if (iterable != null)
        {
            return IterableIterate.zipWithIndex(iterable);
        }
        throw new IllegalArgumentException("Cannot perform a zipWithIndex on null");
    }

    /**
     * @see RichIterable#zipWithIndex(Collection)
     */
    public static <T, R extends Collection<Pair<T, Integer>>> R zipWithIndex(
            Iterable<T> iterable,
            R targetCollection)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).zipWithIndex(targetCollection);
        }
        if (iterable instanceof ArrayList)
        {
            return ArrayListIterate.zipWithIndex((ArrayList<T>) iterable, targetCollection);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.zipWithIndex((List<T>) iterable, targetCollection);
        }
        if (iterable != null)
        {
            return IterableIterate.zipWithIndex(iterable, targetCollection);
        }
        throw new IllegalArgumentException("Cannot perform a zipWithIndex on null");
    }

    /**
     * @see RichIterable#chunk(int)
     */
    public static <T> RichIterable<RichIterable<T>> chunk(Iterable<T> iterable, int size)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).chunk(size);
        }
        if (iterable != null)
        {
            return IterableIterate.chunk(iterable, size);
        }
        throw new IllegalArgumentException("Cannot perform a chunk on null");
    }

    /**
     * @see RichIterable#makeString()
     */
    public static <T> String makeString(Iterable<T> iterable)
    {
        return Iterate.makeString(iterable, ", ");
    }

    /**
     * @see RichIterable#makeString(String)
     */
    public static <T> String makeString(Iterable<T> iterable, String separator)
    {
        return Iterate.makeString(iterable, "", separator, "");
    }

    /**
     * @see RichIterable#makeString(String, String, String)
     */
    public static <T> String makeString(Iterable<T> iterable, String start, String separator, String end)
    {
        Appendable stringBuilder = new StringBuilder();
        Iterate.appendString(iterable, stringBuilder, start, separator, end);
        return stringBuilder.toString();
    }

    /**
     * @see RichIterable#appendString(Appendable)
     */
    public static <T> void appendString(Iterable<T> iterable, Appendable appendable)
    {
        Iterate.appendString(iterable, appendable, ", ");
    }

    /**
     * @see RichIterable#appendString(Appendable, String)
     */
    public static <T> void appendString(Iterable<T> iterable, Appendable appendable, String separator)
    {
        Iterate.appendString(iterable, appendable, "", separator, "");
    }

    /**
     * @see RichIterable#appendString(Appendable, String, String, String)
     */
    public static <T> void appendString(
            Iterable<T> iterable,
            Appendable appendable,
            String start,
            String separator,
            String end)
    {
        if (iterable instanceof MutableCollection)
        {
            ((MutableCollection<T>) iterable).appendString(appendable, start, separator, end);
        }
        else if (iterable instanceof RandomAccess)
        {
            RandomAccessListIterate.appendString((List<T>) iterable, appendable, start, separator, end);
        }
        else if (iterable != null)
        {
            IterableIterate.appendString(iterable, appendable, start, separator, end);
        }
        else
        {
            throw new IllegalArgumentException("Cannot perform an appendString on null");
        }
    }

    public static <T, V extends Comparable<? super V>> T maxBy(Iterable<T> iterable, Function<? super T, ? extends V> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).maxBy(function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.maxBy((List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.maxBy(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform a maxBy on null");
    }

    public static <T, V extends Comparable<? super V>> T minBy(Iterable<T> iterable, Function<? super T, ? extends V> function)
    {
        if (iterable instanceof RichIterable)
        {
            return ((RichIterable<T>) iterable).minBy(function);
        }
        if (iterable instanceof RandomAccess)
        {
            return RandomAccessListIterate.minBy((List<T>) iterable, function);
        }
        if (iterable != null)
        {
            return IterableIterate.minBy(iterable, function);
        }
        throw new IllegalArgumentException("Cannot perform a minBy on null");
    }
}
