package command.concrete;

import command.AbstractCommand;
import net.Handler;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class PortCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, Handler handler, ArrayList<Object> args, Terminal terminal) {
        Integer port = (Integer) args.get(0);

        handler.setPort(port);
    }
}
