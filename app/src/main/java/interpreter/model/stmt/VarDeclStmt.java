package interpreter.model.stmt;
import interpreter.model.PrgState;
import interpreter.model.type.IType;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;

public record VarDeclStmt(String name, IType type) implements IStmt {
    @Override public PrgState execute(PrgState state) throws MyException {
        if (state.getSymTable().isDefined(name))
            throw new StatementException("Variable " + name + " already declared");
        state.getSymTable().put(name, type.defaultValue());
        return state;
    }
    @Override public String toString() { return type + " " + name; }
}