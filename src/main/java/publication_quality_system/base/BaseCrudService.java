package publication_quality_system.base;

public interface BaseCrudService<D, ID> {

    D create(D dto);

    D getById(ID id);

    D update(ID id, D dto);

    void delete(ID id);
}
