package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainServlet extends HttpServlet {
  private PostController controller;

  @Override
  public void init() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    controller = new PostController(service);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    final String pathApi = "/api/posts";
    final String pathApiD = "/api/posts/\\d+";
    // если деплоились в root context, то достаточно этого
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      // primitive routing
      if (method.equals("GET") && path.equals(pathApi)) {
        controller.all(resp);
        return;
      }
      if (method.equals("GET") && path.matches(pathApiD)) {
        // easy way
        final var id = parseId(path);
        controller.getById(id, resp);
        return;
      }
      if (method.equals("POST") && path.equals(pathApi)) {
        controller.save(req.getReader(), resp);
        return;
      }
      if (method.equals("DELETE") && path.matches(pathApiD)) {
        // easy way
        final var id = parseId(path);
        System.out.println(id);
        controller.removeById(id, resp);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  private long parseId(String path) {
    return Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
  }
}