package interpreter.model.adt;
import interpreter.exceptions.ADTException;

import java.util.*;

public class MyStack<T> implements IStack<T> {
    private final Deque<T> stack = new ArrayDeque<>();

    @Override public void push(T v) { stack.push(v); }

    @Override public T pop() throws ADTException {
        if (stack.isEmpty()) throw new ADTException("pop from an empty stack");
        return stack.pop();
    }

    @Override public List<T> toList() {
        return Collections.unmodifiableList(new ArrayList<>(stack));
    }

    @Override public boolean isEmpty() { return stack.isEmpty(); }
    @Override public String toString() { return stack.toString(); }
}