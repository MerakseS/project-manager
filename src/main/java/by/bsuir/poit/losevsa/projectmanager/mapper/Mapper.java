package by.bsuir.poit.losevsa.projectmanager.mapper;

public interface Mapper<T, S> {

    T toEntity(S s);

    S toDto(T t);
}
