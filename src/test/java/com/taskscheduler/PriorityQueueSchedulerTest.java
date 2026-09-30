package com.taskscheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PriorityQueueSchedulerTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private PriorityQueueScheduler scheduler;

    private final User user = mock(User.class);

    @Test
    void getAllTasks_sortsByPriorityThenDeadline() {
        Task low = new Task("Low", 3, "2026-10-01", 30, user);
        Task highLateDeadline = new Task("HighLate", 1, "2026-10-10", 30, user);
        Task highEarlyDeadline = new Task("HighEarly", 1, "2026-10-05", 30, user);

        when(taskRepository.findByUserOrderByPriorityAscDeadlineAsc(user))
                .thenReturn(Arrays.asList(low, highLateDeadline, highEarlyDeadline));

        List<Task> result = scheduler.getAllTasks(user);

        assertEquals("HighEarly", result.get(0).getName());
        assertEquals("HighLate", result.get(1).getName());
        assertEquals("Low", result.get(2).getName());
    }

    @Test
    void getNextTask_skipsCompletedTasks() {
        Task done = new Task("Done", 1, "2026-10-01", 30, user);
        done.setCompleted(true);
        Task pending = new Task("Pending", 2, "2026-10-02", 30, user);

        when(taskRepository.findByUserOrderByPriorityAscDeadlineAsc(user))
                .thenReturn(Arrays.asList(done, pending));

        Task next = scheduler.getNextTask(user);

        assertEquals("Pending", next.getName());
    }

    @Test
    void getNextTask_returnsNullWhenNoTasks() {
        when(taskRepository.findByUserOrderByPriorityAscDeadlineAsc(user))
                .thenReturn(Collections.emptyList());

        assertNull(scheduler.getNextTask(user));
    }

    @Test
    void getTasksForAvailableTime_picksOnlyTasksThatFit() {
        Task a = new Task("A", 1, "2026-10-01", 60, user); 
        Task b = new Task("B", 2, "2026-10-02", 30, user);
        Task c = new Task("C", 3, "2026-10-03", 20, user);

        when(taskRepository.findByUserOrderByPriorityAscDeadlineAsc(user))
                .thenReturn(Arrays.asList(a, b, c));

        List<Task> result = scheduler.getTasksForAvailableTime(user, 50);

        assertEquals(2, result.size());
        assertEquals("B", result.get(0).getName());
        assertEquals("C", result.get(1).getName());
    }
}