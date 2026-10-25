package interpreter.model.stmt;
import interpreter.model.PrgState;
import interpreter.exceptions.MyException;

public interface IStmt {
    PrgState execute(PrgState state) throws MyException;
}