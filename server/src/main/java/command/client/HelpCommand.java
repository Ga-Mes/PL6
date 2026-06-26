package command.client;

import command.AbstractClientCommand;
import data.CollectionManager;
import language.Lexer;
import net.Request;
import net.RequestContext;
import net.RequestStatus;
import net.Response;
import org.slf4j.Logger;

public class HelpCommand extends AbstractClientCommand {
    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        Response response;

        if (Lexer.transform(request) != null) {
            response = new Response(1, """
                help - display a list of available commands.
                info - output information about the collection.
                show - display all elements of the collection in string representation.
                insert key name age description - add a new element with the specified key.
                update id name age description - update the value of the collection element with the specified id.
                remove_key key - remove an element from the collection by its key.
                clear - clear the collection.
                exit - terminate the program.
                register login password - register in system.
                login login password - login in system.
                remove_greater_key key - remove all elements from the collection whose key exceeds the specified one.
                filter_contains_description description - display elements whose description field contains the given substring.
                filter_greater_than_age age - display elements whose age field is greater than the specified value.
                print_unique_color - display the unique values of the color field for all elements in the collection."""
            );
        } else {
            response = new Response(2, "Wrong primitive arguments...");
        }

        context.status = RequestStatus.FINISHED;

        return response;
    }
}
