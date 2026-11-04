package interpreter.model.type;

import interpreter.model.value.IValue;

public record StringType() implements IType{
    @Override public IValue defaultValue() {return StringValue("");}
    @Override public String toString(){return "string";}
}
