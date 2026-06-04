package command;

import net.Handler;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public abstract class AbstractCommand {
    public abstract void execute(boolean[] statuses, Handler handler, ArrayList<Object> args, Terminal terminal);
}
