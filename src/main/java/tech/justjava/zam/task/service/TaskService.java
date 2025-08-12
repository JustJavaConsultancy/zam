package tech.justjava.zam.task.service;

import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tech.justjava.zam.process_instance.domain.ProcessInstance;
import tech.justjava.zam.process_instance.repos.ProcessInstanceRepository;
import tech.justjava.zam.task.domain.Task;
import tech.justjava.zam.task.model.TaskDTO;
import tech.justjava.zam.task.repos.TaskRepository;
import tech.justjava.zam.process.service.NotFoundException;


@Service
public class TaskService {

    @Autowired
    private RepositoryService repositoryService;

    private final TaskRepository taskRepository;
    private final org.flowable.engine.TaskService flowableTaskService;
    private final ProcessInstanceRepository processInstanceRepository;
    private final HistoryService historyService;

    public TaskService(final TaskRepository taskRepository,
                       org.flowable.engine.TaskService taskService, org.flowable.engine.TaskService flowableTaskService, final ProcessInstanceRepository processInstanceRepository, HistoryService historyService) {
        this.taskRepository = taskRepository;
        this.flowableTaskService = flowableTaskService;
        this.processInstanceRepository = processInstanceRepository;
        this.historyService = historyService;
    }

    public List<TaskDTO> findAll() {
        final List<Task> tasks = taskRepository.findAll(Sort.by("id"));
        return tasks.stream()
                .map(task -> mapToDTO(task, new TaskDTO()))
                .toList();
    }
    public void completeTask(String taskId, Map<String,Object> variables) {
        flowableTaskService.complete(taskId,variables);
    }
    public List<org.flowable.task.api.Task> findActiveflowableTasks() {
        return flowableTaskService
                .createTaskQuery()
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
    public List<org.flowable.task.api.Task> findActiveflowableTasksByProcess(String processKey) {
        return flowableTaskService
                .createTaskQuery()
                .processDefinitionKey(processKey)
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list();
    }
    public org.flowable.task.api.Task findTaskById(String taskId) {
        return flowableTaskService
                .createTaskQuery()
                .active()
                .includeTaskLocalVariables()
                .includeProcessVariables()
                .taskId(taskId)
                .singleResult();

    }

    //new addition
    public org.flowable.task.api.Task getTaskByInstanceAndDefinitionKey(String processInstanceId, String taskDefinitionKey){
        return flowableTaskService.
                createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskDefinitionKey)
                .includeProcessVariables()
                .singleResult();
    }

    public List<org.flowable.task.api.Task> getTaskByAssigneeAndProcessDefinitionKey
            (String assignee,String processDefinitionKey) {
        return flowableTaskService
                .createTaskQuery()
                .taskAssignee(assignee)
                .processDefinitionKey(processDefinitionKey)
                .includeProcessVariables()
                .list();
    }

    public List<HistoricTaskInstance> getCompletedTaskByAssigneeAndVariable(String assignee, String processKey,
                                                                            String variableName, String variableValue){
        return historyService
                .createHistoricTaskInstanceQuery()
                .processDefinitionKey(processKey)
                .taskAssignee(assignee)
                .processVariableValueEquals(variableName, variableValue)
                .finished()
                .list();
    }

    public List<HistoricTaskInstance> getCompletedTaskByAssignee(String assignee, String processKey){
        return historyService
                .createHistoricTaskInstanceQuery()
                .processDefinitionKey(processKey)
                .taskAssignee(assignee)
                .finished()
                .list();
    }


    public String getTaskDocumentation(String taskId) {
        org.flowable.task.api.Task task=flowableTaskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("Task not found.");
        }
        String processDefinitionId = task.getProcessDefinitionId();
        String taskDefinitionKey = task.getTaskDefinitionKey();

        // 2) Load BPMN model
        BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = model.getMainProcess();

        // 3) Find UserTask definition
        FlowElement element = process.getFlowElement(taskDefinitionKey);

        if (element instanceof UserTask) {
            UserTask userTask = (UserTask) element;

            // 4) Retrieve documentation
            return userTask.getDocumentation();
        }
        return "Empty Documentation";
    }
    public TaskDTO get(final Long id) {
        return taskRepository.findById(id)
                .map(task -> mapToDTO(task, new TaskDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Long create(final TaskDTO taskDTO) {
        final Task task = new Task();
        mapToEntity(taskDTO, task);
        return taskRepository.save(task).getId();
    }

    public void update(final Long id, final TaskDTO taskDTO) {
        final Task task = taskRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(taskDTO, task);
        taskRepository.save(task);
    }

    public void delete(final Long id) {
        taskRepository.deleteById(id);
    }

    private TaskDTO mapToDTO(final Task task, final TaskDTO taskDTO) {
        taskDTO.setId(task.getId());
        taskDTO.setTaskName(task.getTaskName());
        taskDTO.setTaskForm(task.getTaskForm());
        taskDTO.setTaskVariable(task.getTaskVariable());
        taskDTO.setFields(task.getFields());
        taskDTO.setTaskStatus(task.getTaskStatus());
        taskDTO.setProcessInstance(task.getProcessInstance() == null ? null : task.getProcessInstance().getId());
        return taskDTO;
    }

    private Task mapToEntity(final TaskDTO taskDTO, final Task task) {
        task.setTaskName(taskDTO.getTaskName());
        task.setTaskForm(taskDTO.getTaskForm());
        task.setTaskVariable(taskDTO.getTaskVariable());
        task.setFields(taskDTO.getFields());
        task.setTaskStatus(taskDTO.getTaskStatus());
        final ProcessInstance processInstance = taskDTO.getProcessInstance() == null ? null : processInstanceRepository.findById(taskDTO.getProcessInstance())
                .orElseThrow(() -> new NotFoundException("processInstance not found"));
        task.setProcessInstance(processInstance);
        return task;
    }

}
