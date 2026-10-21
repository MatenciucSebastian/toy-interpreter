package interpreter.model.type;
import interpreter.model.value.IValue;

public interface IType {
    IValue defaultValue();
}

// TODO: BoolType (default value false)