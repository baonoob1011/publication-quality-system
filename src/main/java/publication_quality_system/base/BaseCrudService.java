package publication_quality_system.base;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface BaseCrudService<D, ID> {

    D create(D dto);

    D getById(ID id);

    D update(ID id, D dto);

    void delete(ID id);

    List<D> getAll(Pageable pageable);
}
