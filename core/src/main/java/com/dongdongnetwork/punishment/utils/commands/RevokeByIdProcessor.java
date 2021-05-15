package com.dongdongnetwork.punishment.utils.commands;

import com.dongdongnetwork.punishment.Universal;
import com.dongdongnetwork.punishment.manager.MessageManager;
import com.dongdongnetwork.punishment.utils.Command;
import com.dongdongnetwork.punishment.utils.Punishment;

import java.util.function.Consumer;
import java.util.function.Function;

public class RevokeByIdProcessor implements Consumer<Command.CommandInput> {
    private String path;
    private Function<Integer, Punishment> resolver;

    public RevokeByIdProcessor(String path, Function<Integer, Punishment> resolver) {
        this.path = path;
        this.resolver = resolver;
    }


    @Override
    public void accept(Command.CommandInput input) {
        int id = Integer.parseInt(input.getPrimary());

        Punishment punishment = resolver.apply(id);
        if (punishment == null) {
            MessageManager.sendMessage(input.getSender(), path + ".NotFound",
                    true, "ID", id + "");
            return;
        }

        final String operator = Universal.get().getMethods().getName(input.getSender());
        punishment.delete(operator, false, true);
        MessageManager.sendMessage(input.getSender(), path + ".Done",
                true, "ID", id + "");
    }
}
