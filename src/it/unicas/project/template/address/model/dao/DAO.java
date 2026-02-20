package it.unicas.project.template.address.model.dao;

import it.unicas.project.template.address.model.Luogo;
import java.util.List;

public interface DAO <T>{
    List<T> select(T a) throws DAOException;
    void update(T a) throws DAOException;
    void insert(T a) throws DAOException;
    void delete(T a) throws DAOException;
}
