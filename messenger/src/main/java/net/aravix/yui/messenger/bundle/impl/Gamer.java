package net.aravix.yui.messenger.bundle.impl;

import net.aravix.yui.messenger.bundle.Bundle;

import java.util.UUID;

public class Gamer {
    public record Join(UUID uuid, String server) implements Bundle {}
}