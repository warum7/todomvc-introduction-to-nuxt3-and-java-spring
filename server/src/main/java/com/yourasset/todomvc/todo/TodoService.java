package com.yourasset.todomvc.todo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos() {
        List<Todo> allTodos = todoRepository.findAll();
        List<Todo> sortedTodos = new ArrayList<>(allTodos);
        sortedTodos.sort(Comparator.comparing(Todo::getCreatedAt).reversed());
        return allTodos;
    }

    public void createTodo(Todo todo) throws BadRequestException {
        if (todo.getTitle() == null || todo.getTitle().isEmpty()) {
            throw new BadRequestException("Todo title cannot be empty");
        }
        todoRepository.save(todo);
    }

    public void deleteCompletedTodos() {

        List<Todo> completedTodos = todoRepository.findCompletedTodos();

        todoRepository.deleteAll(completedTodos);
    }

    public void deleteTodoById(String id) throws TodoNotFoundException, BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("Todo id cannot be empty");
        }

        boolean todoExists = todoRepository.existsById(id);

        if (!todoExists) {
            throw new TodoNotFoundException("Todo with id " + id + " does not exist");
        }

        if (id != null) {
            todoRepository.deleteById(id);
        }
    }

    public void updateTodoById(String id, Todo todo) throws TodoNotFoundException, BadRequestException {

        if (id == null || id.isEmpty()) {
            throw new BadRequestException("Todo id cannot be empty");
        }

        Todo existingTodo = todoRepository.findById(id).get();

        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException("Todo with id " + id + " does not exist");
        }

        if (todo.getTitle() != null && !todo.getTitle().isEmpty() && todo.getTitle() != existingTodo.getTitle()) {
            existingTodo.setTitle(todo.getTitle());
        }

        existingTodo.setCompleted(todo.isCompleted());
        existingTodo.setUpdatedAt(LocalDateTime.now());
        todoRepository.save(existingTodo);

    }

    public void toggleAllTodos() {
        List<Todo> allTodos = getAllTodos();

        if (allTodos.stream().allMatch(Todo::isCompleted)) {
            allTodos.forEach(t -> {
                t.setCompleted(false);
                t.setUpdatedAt(LocalDateTime.now());
            });
        } else {
            allTodos.forEach(t -> {
                t.setCompleted(true);
                t.setUpdatedAt(LocalDateTime.now());
            });
        }
        todoRepository.saveAll(allTodos);
    }

    public List<Todo> getAllCompletedTodos() {
        List<Todo> completedTodos = todoRepository.findCompletedTodos();
        List<Todo> sortedTodos = new ArrayList<>(completedTodos);
        sortedTodos.sort(Comparator.comparing(Todo::getCreatedAt).reversed());
        return completedTodos;
    }

    public List<Todo> getAllIncompletedTodos() {
        List<Todo> incompletedTodos = todoRepository.findActiveTodos();
        List<Todo> sortedTodos = new ArrayList<>(incompletedTodos);
        sortedTodos.sort(Comparator.comparing(Todo::getCreatedAt).reversed());
        return incompletedTodos;
    }

}
