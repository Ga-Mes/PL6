package command.concrete;

import command.AbstractCommand;
import command.CommandType;
import net.Handler;
import net.Request;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class RemoveKeyCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, Handler handler, ArrayList<Object> args, Terminal terminal) {
        Request request = new Request(CommandType.REMOVE_KEY, args);

        String response = handler.process(request, terminal);

        System.out.println(response);
    }
}
