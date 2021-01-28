package dev.spaceseries.api.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class Utility {
    public static Long stringToMillisDuration(String s) {
        StringBuilder builder = new StringBuilder();
        int seconds = 0;
        int minutes = 0;
        int hours = 0;
        int days = 0;
        int weeks = 0;
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                builder.append(c);
            } else {
                switch (c) {
                    case 's':
                        if (builder.length() != 0) {
                            seconds += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    case 'm':
                        if (builder.length() != 0) {
                            minutes += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    case 'h':
                        if (builder.length() != 0) {
                            hours += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    case 'd':
                        if (builder.length() != 0) {
                            days += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    case 'w':
                        if (builder.length() != 0) {
                            weeks += Integer.parseInt(builder.toString());
                            builder = new StringBuilder();
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Not a valid duration format.");
                }
            }
        }
        return 1000L * (seconds + minutes * 60 + hours * 60 * 60 + days * 24 * 60 * 60 + weeks * 7 * 24 * 60 * 60);
    }

    public static Long stringToTimeUnit(String s, TimeUnit timeUnit) {
        Long millis = stringToMillisDuration(s);
        return timeUnit.convert(millis, TimeUnit.MILLISECONDS);
    }

    public static String niceFormat(Long seconds, boolean shrink) {
        if (seconds == 0) return shrink ? "0s" : "0 seconds";
        int days = 0;
        int hours = 0;
        int minutes = 0;
        long secs;
        while (seconds >= 24 * 60 * 60) {
            ++days;
            seconds -= 24 * 60 * 60;
        }
        while (seconds >= 60 * 60) {
            ++hours;
            seconds -= 60 * 60;
        }
        while (seconds >= 60) {
            ++minutes;
            seconds -= 60;
        }
        secs = seconds;
        StringBuilder sb = new StringBuilder();
        if (days != 0) {
            sb.append(days);
            if (shrink)
                sb.append("d");
            else
                sb.append(days == 1 ? " day" : " days");
        }
        if (hours != 0) {
            if (!shrink && sb.length() != 0) sb.append(" ");
            sb.append(hours);
            if (shrink)
                sb.append("h");
            else
                sb.append(hours == 1 ? " hour" : " hours");
        }
        if (minutes != 0) {
            if (!shrink && sb.length() != 0) sb.append(" ");
            sb.append(minutes);
            if (shrink)
                sb.append("m");
            else
                sb.append(minutes == 1 ? " minute" : " minutes");
        }
        if (secs != 0) {
            if (!shrink && sb.length() != 0) sb.append(" ");
            sb.append(secs);
            if (shrink)
                sb.append("s");
            else
                sb.append(secs == 1 ? " second" : " seconds");
        }
        return sb.toString();
    }

    public static String niceFormat(Long seconds) {
        return niceFormat(seconds, false);
    }

    public static String itemStackArrayToBase64(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception var4) {
            throw new RuntimeException("Unable to save item stacks.", var4);
        }
    }

    public static ItemStack[] itemStackArrayFromBase64(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];
            for (int i = 0; i < items.length; ++i) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            dataInput.close();
            return items;
        } catch (ClassNotFoundException | IOException var5) {
            throw new RuntimeException("Unable to decode class type.", var5);
        }
    }

    public static String formatMoney(Double number) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        int i = number.intValue() / 1000;

        if (i == 0)
            return trimLastDigit(formatter.format(number));

        number = Math.round(number / 100) / 10.0;

        i = number.intValue() / 1000;

        if (i == 0)
            return trimLastDigit(formatter.format(number)) + "K";

        number = Math.round(number / 100) / 10.0;

        i = number.intValue() / 1000;

        if (i == 0)
            return trimLastDigit(formatter.format(number)) + "M";

        number = Math.round(number / 100) / 10.0;
        return trimLastDigit(formatter.format(number)) + "B";
    }

    private static String trimLastDigit(String s) {
        String[] split = s.split("\\.", 2);
        String decimal = split[1].substring(0, Math.min(split[1].length(), 1));
        if (decimal.length() == 0) decimal += "0";

        return split[0] + "." + decimal;
    }
}
