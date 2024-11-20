package org.example;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.example.controllers.Controller;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

public class App {
    private Javalin app;

    public static void main(String[] args) {
        var app = new App().javalinApp();
        app.start(8081);
    }

    public App() {

        app = Javalin.create(
                config -> {
                    //config.staticFiles.add("/public", Location.CLASSPATH);
                    var resolver = new ClassLoaderTemplateResolver();
                    resolver.setPrefix("/templates/");
                    resolver.setSuffix(".html");
                    resolver.setTemplateMode("HTML");
                    var engine = new TemplateEngine();
                    engine.setTemplateResolver(resolver);
                    config.fileRenderer(new JavalinThymeleaf(engine));
                }
        );

        app
                .get("/", Controller::renderForm)
                .post("/", Controller::storeMessage)
                .get("/{noteId}", Controller::retrieveMessage);
    }

    public Javalin javalinApp() {
        return app;
    }
}