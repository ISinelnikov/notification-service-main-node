package notification.service.vaadin.common;

import notification.service.backend.repository.base.ModelModificationException;

import java.io.Serializable;

public interface CrudService<T> extends Serializable {
    void saveModel(T model) throws ModelModificationException;

    void updateModel(T model) throws ModelModificationException;

    void removeModel(T model) throws ModelModificationException;

    void refreshAll();
}
