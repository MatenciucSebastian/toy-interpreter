package interpreter.model.stmt;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.value.IValue;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;

public record AssignStmt(String id, IExp exp) implements IStmt {
    @Override public PrgState execute(PrgState state) throws MyException {
        var symTable = state.getSymTable();
        if (!symTable.isDefined(id))
            throw new StatementException("Variable " + id + " was not declared");

        IValue val = exp.eval(symTable);
        if (!val.getType().equals(symTable.lookup(id).getType()))
            throw new StatementException("Declared type and assigned expression type do not match");

        symTable.update(id, val);
        return state;
    }
    @Override public String toString() { return id + "=" + exp; }
}