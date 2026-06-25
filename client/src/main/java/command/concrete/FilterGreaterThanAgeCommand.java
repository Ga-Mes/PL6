package command.concrete;

import command.AbstractCommand;
import command.CommandType;
import net.Handler;
import net.Request;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class FilterGreaterThanAgeCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, Handler handler, ArrayList<Object> args, Terminal terminal) {
        Request request = new Request(CommandType.FILTER_GREATER_THAN_AGE, args, handler.login, handler.password);

        String response = handler.process(request, terminal);

        System.out.println(response);
    }
}
