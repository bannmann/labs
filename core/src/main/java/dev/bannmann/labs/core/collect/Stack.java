package dev.bannmann.labs.core.collect;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import com.google.common.collect.Iterators;
import net.jcip.annotations.NotThreadSafe;

/**
 * A last-in-first-out stack of objects. This class is meant to be a modern replacement for Java 1.0's
 * {@link java.util.Stack} class.
 *
 * <p>While this is just a wrapper for a {@link Deque}, it offers the following advantages over that interface and its
 * implementations:
 * <ol>
 *     <li>
 *         This class only implements the minimal set of write methods required for LIFO operation, reducing the chance
 *         of inconsistent or unintended usage at call sites.
 *     </li>
 *     <li>
 *         {@link #pop()} and {@link #peek()} return {@link Optional}. This not only forces the call site to explicitly
 *         handle 'stack is empty' case, but also enables use of Optional's flexible filtering and mapping capabilities.
 *     </li>
 *     <li>
 *         {@link #contains(Object)} uses the type parameter instead of {@link Object}, making call sites rely on
 *         compiler type checks for correctness instead of IDEs and linters.
 *     </li>
 * </ol>
 */
@NotThreadSafe
@EqualsAndHashCode
public final class Stack<E> implements Iterable<E>
{
    private final Deque<E> deque = new ArrayDeque<>();

    /**
     * Pushes an element onto the top of this stack.
     *
     * @param element the element to be pushed onto this stack, cannot be {@code null}
     *
     * @throws NullPointerException if the given element is {@code null}
     */
    public void push(@NonNull E element)
    {
        deque.addFirst(element);
    }

    /**
     * Removes the topmost element from this stack and returns it.
     *
     * @return an {@code Optional} containing the element that was removed, or an empty {@code Optional} if the stack was empty.
     */
    public Optional<E> pop()
    {
        return Optional.ofNullable(deque.pollFirst());
    }

    /**
     * Gets the topmost element from this stack without removing it.
     *
     * @return an {@code Optional} containing the topmost element, or an empty {@code Optional} if the stack is empty.
     */
    public Optional<E> peek()
    {
        return Optional.ofNullable(deque.peekFirst());
    }

    /**
     * Removes all elements from this stack.
     */
    public void clear()
    {
        deque.clear();
    }

    /**
     * Returns {@code true} if this stack contains the given element.
     *
     * @param element element whose presence in this stack is to be tested, cannot be {@code null}
     *
     * @return {@code true} if this deque contains the given element
     *
     * @throws NullPointerException if the given element is {@code null}
     */
    public boolean contains(@NonNull E element)
    {
        return deque.contains(element);
    }

    public boolean isEmpty()
    {
        return deque.isEmpty();
    }

    public int size()
    {
        return deque.size();
    }

    public Stream<E> stream()
    {
        return deque.stream();
    }

    @Override
    public Spliterator<E> spliterator()
    {
        return deque.spliterator();
    }

    /**
     * Returns an iterator over the elements of this stack, starting with the topmost one.
     *
     * @return an iterator that does not support the {@link Iterator#remove()} method
     */
    @Override
    public Iterator<E> iterator()
    {
        return Iterators.unmodifiableIterator(deque.iterator());
    }

    /**
     * Returns a string representation of this stack. The string representation consists of a list of the
     * stack's elements starting with the topmost element, enclosed in square brackets ({@code "[]"}). Adjacent elements
     * are separated by the characters {@code ", "} (comma and space). Elements are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this stack
     */
    @Override
    public String toString()
    {
        return deque.toString();
    }
}
