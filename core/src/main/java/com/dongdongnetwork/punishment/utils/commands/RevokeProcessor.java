package com.dongdongnetwork.punishment.utils.commands;

import com.dongdongnetwork.punishment.Universal;
import com.dongdongnetwork.punishment.manager.MessageManager;
import com.dongdongnetwork.punishment.utils.Command;
import com.dongdongnetwork.punishment.utils.Punishment;
import com.dongdongnetwork.punishment.utils.PunishmentType;

import java.util.function.Consumer;

import static com.dongdongnetwork.punishment.utils.CommandUtils.getPunishment;
import static com.dongdongnetwork.punishment.utils.CommandUtils.processName;

public class RevokeProcessor implements Consumer<Command.CommandInput> {
    private PunishmentType type;

    public RevokeProcessor(PunishmentType type) {
        this.type = type;
    }

    @Override
    public void accept(Command.CommandInput input) {
        String name = input.getPrimary();

        String target = name;
        if(!target.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
            target = processName(input);
            if (target == null)
                return;
        }

        Punishment punishment = getPunishment(target, type);
        if (punishment == null) {
            MessageManager.sendMessage(input.getSender(), "Un" + type.getName() + ".NotPunished",
                    true, "NAME", name);
            return;
        }

        final String operator = Universal.get().getMethods().getName(input.getSender());
        punishment.delete(operator, false, true);
        MessageManager.sendMessage(input.getSender(), "Un" + type.getName() + ".Done",
                true, "NAME", name);
    }
}
