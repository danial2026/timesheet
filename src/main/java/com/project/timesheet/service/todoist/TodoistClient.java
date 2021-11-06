package com.project.timesheet.service.todoist;

import com.project.timesheet.dto.TodoistTask;
import com.project.timesheet.exception.BusinessServiceException;

import java.util.List;

public interface TodoistClient {

    TodoistTask getTask(String taskKey) throws BusinessServiceException;

    List<TodoistTask> getActiveTask() throws BusinessServiceException;
}
