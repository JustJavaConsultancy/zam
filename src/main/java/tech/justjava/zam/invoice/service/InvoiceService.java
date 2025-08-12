package tech.justjava.zam.invoice.service;

import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import tech.justjava.zam.task.service.TaskService;

import java.util.Map;


@Service("invoiceService")
public class InvoiceService {

    private final TaskService taskService;

    public InvoiceService(final TaskService taskService){
        this.taskService = taskService;
    }

    public void seniorReviewTask(String taskId, Map<String, Object> variables){

        Task singleTask = taskService.findTaskById(taskId);
        System.out.println("This is the senior review form with variables:::" + variables);
        taskService.completeTask(singleTask.getId(), variables);
    }

    public void editInvoiceTask(String taskId, Map<String, Object> variables){

        Task singleTask = taskService.findTaskById(taskId);
        System.out.println("This is the editInvoice task with variables:::" + variables);
        taskService.completeTask(singleTask.getId(), variables);
    }
}
