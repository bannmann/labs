package dev.bannmann.labs.core.collect;

import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.google.common.collect.Iterators;
import com.google.errorprone.annotations.MustBeClosed;
import dev.bannmann.labs.annotations.SuppressWarningsRationale;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public final class MemoStack<T> implements Iterable<T>
{
    public interface MemoHandle extends AutoCloseable
    {
        @Override
        void close();
    }

    @RequiredArgsConstructor
    private static class Memo<T>
    {
        @SuppressWarnings("java:S3985")
        @SuppressWarningsRationale("Sonar thinks this class is unused")
        private class HandleImpl implements MemoHandle
        {
            @Override
            public void close()
            {
                closer.accept(Memo.this);
            }
        }

        @Getter
        private final T content;

        private final Consumer<Memo<?>> closer;

        @Getter
        private final HandleImpl handle = new HandleImpl();
    }

    private final Stack<Memo<T>> memos = new Stack<>();

    @MustBeClosed
    public MemoHandle push(T contents)
    {
        Memo<T> memo = new Memo<>(contents, this::remove);
        memos.push(memo);
        return memo.getHandle();
    }

    private void remove(Memo<?> stackableMemo)
    {
        if (!memos.isEmpty() &&
            memos.peek()
                .filter(top -> top == stackableMemo)
                .isEmpty())
        {
            throw new IllegalStateException("Attempt to close memo that is not the last to be created");
        }
        memos.pop();
    }

    public Optional<T> peek()
    {
        return memos.peek()
            .map(Memo::getContent);
    }

    public boolean isEmpty()
    {
        return memos.isEmpty();
    }

    public int size()
    {
        return memos.size();
    }

    public Stream<T> stream()
    {
        return memos.stream().map(Memo::getContent);
    }

    @Override
    public Spliterator<T> spliterator()
    {
        return Spliterators.spliterator(iterator(), size(), Spliterator.NONNULL | Spliterator.ORDERED);
    }

    @Override
    public Iterator<T> iterator()
    {
        return Iterators.transform(memos.iterator(), Memo::getContent);
    }
}
