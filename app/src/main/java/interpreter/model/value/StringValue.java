package interpreter.model.value;

import interpreter.model.type.IType;

public record StringValue(String val) implements IValue {
    @Override public IType getType() { return new StringType(); }
    @Override public String toString() { return "\"" + val + "\""; }
}
