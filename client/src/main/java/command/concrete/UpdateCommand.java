package command.concrete;

import base.DragonCreator;
import command.AbstractCommand;
import command.CommandType;
import net.Handler;
import net.Request;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class UpdateCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, Handler handler, ArrayList<Object> args, Terminal terminal) {
        if (DragonCreator.check(args, 1)) {
            Request request = new Request(CommandType.UPDATE, args, handler.login, handler.password);

            String response = handler.process(request, terminal);

            System.out.println(response);
        }
    }
}
