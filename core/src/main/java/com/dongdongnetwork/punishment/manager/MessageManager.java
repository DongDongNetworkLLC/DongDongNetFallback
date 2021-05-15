package com.dongdongnetwork.punishment.manager;

import com.dongdongnetwork.punishment.MethodInterface;
import com.dongdongnetwork.punishment.Universal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageManager {

    private static MethodInterface mi() {
    	return Universal.get().getMethods();
    }
    public static String getMessage(String path, String... parameters) {
    	MethodInterface mi = mi();
        String str = mi.getString(mi.getMessages(), path);
        if (str == null) {
            str = "Failed! See console for details!";
            System.out.println("!! Message-Error!\n"
                    + "In order to solve the problem please:"
                    + "\n  - Check the Message.yml-File for any missing or double \" or '"
                    + "\n  - Visit yamllint.com to  validate your Message.yml"
                    + "\n  - Delete the message file and restart the server");
        } else {
            str = replace(str, parameters).replace('&', '§');
        }
        return str;
    }
    public static String getMessage(String path, boolean prefix, String... parameters) {
    	MethodInterface mi = mi();
        String prefixStr = "";
        if(prefix && !mi.getBoolean(mi.getConfig(), "Disable Prefix", false))
            prefixStr = getMessage("General.Prefix")+" ";

        return prefixStr+getMessage(path, parameters);
    }
    public static List<String> getLayout(Object file, String path, String... parameters) {
    	MethodInterface mi = mi();
        if (mi.contains(file, path)) {
            List<String> list = new ArrayList<>();
            for (String str : mi.getStringList(file, path)) {
                list.add(replace(str, parameters).replace('&', '§'));
            }
            return list;
        }
        String fileName = mi.getFileName(file);
		System.out.println("!! Message-Error in " + fileName + "!\n"
		        + "In order to solve the problem please:"
		        + "\n  - Check the " + fileName + "-File for any missing or double \" or '"
		        + "\n  - Visit yamllint.com to  validate your " + fileName
		        + "\n  - Delete the message file and restart the server");
		return Collections.singletonList("Failed! See console for details!");
    }
    public static void sendMessage(Object receiver, String path, boolean prefix, String... parameters) {
    	MethodInterface mi = mi();
        final String message = getMessage(path, parameters);
        if(!message.isEmpty()) {
            final String prefixString = prefix && !mi.getBoolean(mi.getConfig(), "Disable Prefix", false) ? getMessage("General.Prefix") + " " : "";
            mi.sendMessage(receiver, prefixString + message);
        }
    }

    private static String replace(String str, String... parameters) {
        for (int i = 0; i < parameters.length - 1; i = i + 2) {
            str = str.replaceAll("%" + parameters[i] + "%", parameters[i + 1]);
        }
        return str;
    }
}
