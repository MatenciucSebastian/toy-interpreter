package interpreter.repository;

import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {
    @TempDir
    Path dir;

    private static long countStates(String log) {
        return log.lines().filter("ExeStack:"::equals).count();
    }

    @Test
    void getCrtPrgReturnsTheInitialState() {
        PrgState state = PrgState.of(new NopStmt());
        var repo = new Repository(state, dir.resolve("log.txt").toString());
        assertSame(state, repo.getCrtPrg());
    }

    @Test
    void logPrgStateExecWritesTheCurrentStateToTheLogFile() throws Exception {
        Path log = dir.resolve("log.txt");
        PrgState state = PrgState.of(new NopStmt());
        new Repository(state, log.toString()).logPrgStateExec();

        String content = Files.readString(log);
        assertTrue(content.startsWith(state.toLogString()));
        assertEquals(1, countStates(content));
    }

    @Test
    void logPrgStateExecAppendsInsteadOfOverwriting() throws Exception {
        Path log = dir.resolve("log.txt");
        var repo = new Repository(PrgState.of(new NopStmt()), log.toString());
        repo.logPrgStateExec();
        repo.logPrgStateExec();
        assertEquals(2, countStates(Files.readString(log)));
    }

    @Test
    void logToAPathThatCannotBeWrittenThrows() {
        var repo = new Repository(PrgState.of(new NopStmt()), dir.toString());
        var ex = assertThrows(RepositoryException.class, repo::logPrgStateExec);
        assertTrue(ex.getMessage().contains("cannot write the log file"));
    }
}
