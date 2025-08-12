package tech.justjava.zam.process_instance.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.justjava.zam.process.domain.Process;
import tech.justjava.zam.process_instance.domain.ProcessInstance;


public interface ProcessInstanceRepository extends JpaRepository<ProcessInstance, Long> {

    ProcessInstance findFirstByProcess(Process process);

}
