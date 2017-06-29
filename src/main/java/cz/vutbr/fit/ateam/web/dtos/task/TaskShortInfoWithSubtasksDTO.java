package cz.vutbr.fit.ateam.web.dtos.task;


import cz.vutbr.fit.ateam.persistence.models.Task;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic info about tasks, but there are task's subtasks.
 */
public class TaskShortInfoWithSubtasksDTO extends TaskShortInfoDTO {
    private List<TaskShortInfoWithSubtasksDTO> subtasks;

    public TaskShortInfoWithSubtasksDTO(Task task) {
        super(task);
        if (task.getSubTasks() != null) {
            subtasks = task.getSubTasks().stream()
                    .filter(t -> !t.equals(task))
                    .map(TaskShortInfoWithSubtasksDTO::new)
                    .sorted(TaskShortInfoDTO::compareByDueDate)
                    .collect(Collectors.toList());
        } else {
            subtasks = Collections.EMPTY_LIST;
        }
    }

    public List<TaskShortInfoWithSubtasksDTO> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(List<TaskShortInfoWithSubtasksDTO> subtasks) {
        this.subtasks = subtasks;
    }
}
