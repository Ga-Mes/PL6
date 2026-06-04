package command.concrete;

import command.AbstractCommand;
import net.Handler;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class ExitCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, Handler handler, ArrayList<Object> args, Terminal terminal) {
        statuses[0] = false;

        System.out.println("Finishing work...");
    }
}
