package interpreter.controller;
import interpreter.repository.IRepository;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;

public class Controller {
    private final IRepository repo;
    private final boolean displayFlag;

    public Controller(IRepository repo, boolean displayFlag) {
        this.repo = repo;
        this.displayFlag = displayFlag;
    }

    public PrgState oneStep(PrgState state) throws MyException {
        var stack = state.getExeStack();
        if (stack.isEmpty()) throw new StatementException("Program state stack is empty");
        IStmt crtStmt = stack.pop();
        return crtStmt.execute(state);
    }

    public void allStep() throws MyException {
        PrgState prg = repo.getCrtPrg();
        if (displayFlag) System.out.println(prg);

        while (!prg.getExeStack().isEmpty()) {
            oneStep(prg);
            if (displayFlag) System.out.println(prg);
        }
    }
}