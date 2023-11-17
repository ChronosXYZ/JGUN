package io.github.chronosx88.JGUN.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Pair<K, V> {
    private K first;
    private V second;
}