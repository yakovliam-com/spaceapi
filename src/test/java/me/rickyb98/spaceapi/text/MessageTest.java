package me.rickyb98.spaceapi.text;

import com.yakovliam.spaceapi.text.FakeCommandSender;
import com.yakovliam.spaceapi.text.Message;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

public class MessageTest {

    private static Message TEST = Message.builder("test")
            .addLine("This is the &bfirst &fline! You can $&ahover$ over this text, ")
            .addLine("while you can $&cclick$ this one. Got it, $&e%player%$?")
            .addLine("$&bso what if i write a very long line? will the color preserve through the lines? there's one" +
                    " way to test this, which is write a very long text like this one and send it to myself. the quick " +
                    "brown fox jumps over the lazy dog.$")
            .addExtra(new Message.Extra().withTooltip(Arrays.asList("beautiful.", "tooltip.", "&bwith colors.")))
            .addExtra(new Message.Extra().withAction(Message.Extra.ClickAction.RUN_COMMAND, "/kill"))
            .addExtra(new Message.Extra()
                    .withTooltip(Collections.singletonList("comb&aoooo"))
                    .withAction(Message.Extra.ClickAction.SUGGEST, "/stop"))
            .addExtra(new Message.Extra().withTooltip("jacob wanted me to", "put this here."))
            .build();

    @Test
    public void sendMessage() {
        FakeCommandSender fs = new FakeCommandSender(null);
        TEST.msg(Collections.singletonList(fs),
                "%player%", "gamer");

        System.out.println(fs.getMessages());
    }
}
