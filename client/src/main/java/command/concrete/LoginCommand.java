package command.concrete;

import command.AbstractCommand;
import net.Handler;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class LoginCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, Handler handler, ArrayList<Object> args, Terminal terminal) {
        handler.login = (String) args.get(0);
        handler.password = (String) args.get(1);
    }
}
