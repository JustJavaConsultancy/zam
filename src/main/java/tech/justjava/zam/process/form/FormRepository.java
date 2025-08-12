package tech.justjava.zam.process.form;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormRepository extends JpaRepository<Form, Long> {
    Optional<Form> findByFormName(String formName);

    Optional<Form> findByFormCode(String formCode);

    List<Form> findByProcessKey(String processKey);


}