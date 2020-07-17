package com.px.tool.infrastructure.utils;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class CollectionUtils {
    /**
     * find elements in c1 not in c2
     */
    public static <E> Set<E> difference(Set<E> c1, Set<E> c2) {
        return c1.stream()
                .filter(e1 -> c2.contains(e1))
                .collect(Collectors.toSet());
    }

    public static boolean isEmpty(Collection<?> e) {
        return org.springframework.util.CollectionUtils.isEmpty(e);
    }
}
