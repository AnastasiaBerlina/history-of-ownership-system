package com.anastasia.project.configs;

import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class GenericModelMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    private GenericModelMapper() {
    }

    public static <T, E> List<T> convertList(List<E> list, Class<T> type) {
        return new ArrayList<>(list.stream()
                .map(entity -> modelMapper.map(entity, type))
                .collect(Collectors.toList()));
    }

    public static <T, E> T convertToClass(E entity, Class<T> type) {
        return modelMapper.map(entity, type);
    }


}
