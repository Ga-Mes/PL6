package net;

import command.CommandType;

import java.util.ArrayList;

public record Request(CommandType type, ArrayList<Object> items) {
}
