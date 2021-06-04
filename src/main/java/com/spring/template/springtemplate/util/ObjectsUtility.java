package com.spring.template.springtemplate.util;

import com.spring.template.springtemplate.exception.ExceptionStatus;
import com.spring.template.springtemplate.exception.ProjectException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Ali Mojahed on 6/3/2021
 * @project spring-template
 **/


public class ObjectsUtility {

    public static boolean isNullOrEmpty(String input) {
        return input == null || input.isEmpty() || input.trim().isEmpty();
    }

    public static <T> boolean isNullOrEmpty(Collection<T> input) {
        return input == null || input.isEmpty();
    }

    public static <K, T> boolean isNullOrEmpty(Map<K, T> input) {
        return input == null || input.isEmpty();
    }

    public static <T> boolean isNullOrEmpty(T[] input) {
        return input == null || input.length <= 0;
    }

    public static boolean isNullOrEmpty(Long input) {
        return input == null || input == 0;
    }

    public static boolean isPositive(int number) {
        return number >= 0;
    }

    public static boolean isPositive(long number) {
        return number >= 0;
    }


    public static void checkPagination(int size, int offset) throws ProjectException {
        if (!ObjectsUtility.isPositive(offset) || !ObjectsUtility.isPositive(size)) {
            throw new ProjectException(ExceptionStatus.INVALID_PAGINATION_PARAM);
        }
    }

    public static <E extends Enum<E>> boolean isValidParam(String value, Class<E> enumClass) {
        for (E e : enumClass.getEnumConstants()) {
            if (e.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <T extends Annotation, E> List<String> getFieldName(Class<E> t, Class<T> annotation) {
        return Arrays.stream(t.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(annotation))
                .map(Field::getName).collect(Collectors.toList());
    }

}
