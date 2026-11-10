package interpreter.model.stmt;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import java.io.BufferedReader;
import java.io.IOException;

public record ReadFileStmt(IExp exp, String varName) implements IStmt {
    @Override public PrgState execute(PrgState state) throws MyException {
        var symTable = state.getSymTable();
        if (!symTable.isDefined(varName))
            throw new StatementException("readFile: variable " + varName + " was not declared");
        if (!symTable.lookup(varName).getType().equals(new IntType()))
            throw new StatementException("readFile: variable " + varName + " is not of type int");

        if (!(exp.eval(symTable) instanceof StringValue fileName))
            throw new StatementException("readFile: " + exp + " is not a string");

        var fileTable = state.getFileTable();
        if (!fileTable.isDefined(fileName))
            throw new StatementException("readFile: " + fileName + " is not open");
        BufferedReader reader = fileTable.lookup(fileName);

        String line = null;
        int n;
        try {
            line = reader.readLine();
            n = (line == null) ? 0 : Integer.parseInt(line.strip());
        } catch (IOException e) {
            throw new StatementException("readFile: cannot read from " + fileName + ": " + e.getMessage());
        } catch (NumberFormatException e) {
            throw new StatementException("readFile: the line \"" + line + "\" of " + fileName + " is not an integer");
        }

        symTable.update(varName, new IntValue(n));
        return state;
    }
    @Override public String toString() { return "readFile(" + exp + ", " + varName + ")"; }
}
