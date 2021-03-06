package jn.rocbot.commands.modcommands;

import jn.rocbot.IDs;
import jn.rocbot.commands.common.Command;
import jn.rocbot.commands.common.CommandConfig;
import jn.rocbot.commands.common.CommandType;
import jn.rocbot.commands.common.PREFIXES;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.StringJoiner;

public class WarnCommand implements Command{

    @Override
    public void action(String[] args, MessageReceivedEvent event) {
        if(args.length == 0){
            event.getTextChannel().sendMessage(String.format("You must specify someone to be warned, use `%shelp warn` for more details", PREFIXES.NORMAL.PREFIX)).complete();
            return;
        }

        StringJoiner joined = new StringJoiner(" ");
        for(String string : args) joined.add(string);

        String user;

        if(joined.toString().contains("|"))
            user = joined.toString().substring(0, joined.toString().indexOf('|') - 1).trim();
        else
            user = joined.toString().trim();

        boolean ignoreCase = user.startsWith("!");
        if(ignoreCase) user = user.replaceFirst("!", "");

        String message;

        if(joined.toString().contains("|"))
            message = String.format(
                    "You have been warned by a moderator, reason: %s",
                    joined.toString().substring(
                            joined.toString().indexOf("|") + 1
                    ).replace("|", "")
            ).trim();
        else message = "You have been warned by a moderator";

        try {
            if (event.getGuild().getMemberById(Long.valueOf(user)) != null)
                warnMember(event.getGuild().getMemberById(Long.valueOf(user)), message);

            return;
        } catch (NumberFormatException ignore) {}

        if (!event.getGuild().getMembersByName(user, ignoreCase).isEmpty()) {
            List<Member> result = event.getGuild().getMembersByName(user, ignoreCase);
            if (result.size() > 1) {
                event.getTextChannel().sendMessage(
                        "More than one member found by the provided name!\n" +
                                "Operation cancelled\n" +
                                "Make sure you get the case right and append a \"!\" to the start, or use the id (long number) of the member to get it right"
                ).complete(); return;
            } warnMember(result.get(0), message);

        } else if (!event.getGuild().getMembersByNickname(user, ignoreCase).isEmpty()) {
            List<Member> result = event.getGuild().getMembersByNickname(user, ignoreCase);
            if (result.size() > 1) {
                event.getTextChannel().sendMessage(
                        "More than one member found by the provided name!\n" +
                                "Operation cancelled\n" +
                                "Make sure you get the case right and append an \"!\" to the start, or use the id (long number) of the member to get it right"
                ).complete(); return;
            } warnMember(result.get(0), message);
        } else {
            event.getTextChannel().sendMessage(String.format("Found no member \"%s\"", user)).complete();
        }
    }

    private void warnMember(Member member, String message){
        member.getUser().openPrivateChannel().complete().sendMessage(message).complete();

        String reason_provided;
        if(message.equals("You have been warned by a moderator")) reason_provided = "None";
        else reason_provided = message;

        member.getGuild().getTextChannelById(
                IDs.CHANNELS.get(IDs.ID_KEY.CHANNEL_GP2_MOD_LOGS)
        ).sendMessage(
                String.format(
                        "Member %s has been warned\nReason: %s",
                        member.getUser().getName(),
                        reason_provided.replace("You have been warned by a moderator, reason: ", "")
                )
        ).complete();
    }

    @Override
    public String help() {
        return String.format(
                "Usage: %swarn Username/Nickname/user-longId [|Reason]\n" +
                    "\tThe provided member is by default searched for ignoring case, if you want the bot to search case sensitive append a \"!\" to the beginning of the name\n" +
                    "\tExamples:\n" +
                        "\t\t%swarn !Bob\n" +
                        "\t\t%swarn I_like_fish1234 | Excessive forcing of fishiness upon others",

                PREFIXES.MODERATOR.PREFIX, PREFIXES.MODERATOR.PREFIX, PREFIXES.MODERATOR.PREFIX
        );
    }

    private CommandConfig config = new CommandConfig(CommandType.MOD, false);

    @Override
    public CommandConfig getConfig() {
        return config;
    }
}
