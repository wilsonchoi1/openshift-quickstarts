package org.openshift.quickstarts.todolist.servlet;

import org.openshift.quickstarts.todolist.model.TodoEntry;
import org.openshift.quickstarts.todolist.service.TodoListService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 *
 */
public class MainServlet extends HttpServlet {

    private TodoListService todoListService = new TodoListService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String PRESHARED_SECRET = getServletContext().getInitParameter("PRESHARED_SECRET");
        String ENC_KEY= getServletContext().getInitParameter("ENC_KEY");
        System.out.println("PRESHARED_SECRET="+PRESHARED_SECRET);
        System.out.println("ENC_KEY="+ENC_KEY);
        resp.setContentType("text/html; charset=UTF-8");
        PrintWriter out = resp.getWriter();
        BufferedReader reader = new BufferedReader(new InputStreamReader(req.getServletContext().getResourceAsStream("/WEB-INF/index.html"), "UTF-8"));
        try {
            String line;
            boolean insideLoop = false;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("<!-- begin repeat for each entry -->")) {
                    insideLoop = true;
                } else if (line.trim().equals("<!-- end repeat for each entry -->")) {
                    insideLoop = false;
                    String entryTemplate = sb.toString();
                    for (TodoEntry entry : todoListService.getAllEntries()) {
                        out.println(
                                entryTemplate
                                        .replace("{{ summary }}", escapeHtml(entry.getSummary()))
                                        .replace("{{ description }}", escapeHtml(entry.getDescription()))
                        );
                    }
                } else if (insideLoop) {
                    sb.append(line).append("\n");
                } else {
                    out.println(line);
                }
            }
        } finally {
            reader.close();
        }
    }

    private String escapeHtml(String text) {
        return text.replace("<", "&lt;");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String summary = req.getParameter("summary");
        String description = req.getParameter("description");

        todoListService.addEntry(new TodoEntry(summary, description));

        resp.sendRedirect("index.html");
    }
}
