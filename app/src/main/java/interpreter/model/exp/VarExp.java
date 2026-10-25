package interpreter.model.exp;
import interpreter.model.adt.IDictionary;
import interpreter.model.value.IValue;
import interpreter.exceptions.MyException;

public record VarExp(String id) implements IExp {
    @Override public IValue eval(IDictionary<String, IValue> symTable) throws MyException {
        return symTable.lookup(id);
    }
    @Override public String toString() { return id; }
}