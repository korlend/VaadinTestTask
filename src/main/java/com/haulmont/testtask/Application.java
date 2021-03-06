package com.haulmont.testtask;

import com.haulmont.testtask.UI.models.UserQualifier;
import com.haulmont.testtask.UI.views.*;
import com.haulmont.testtask.UI.components.HeaderComponent;
import com.haulmont.testtask.UI.models.RouteLink;
import com.haulmont.testtask.models.RecipePriority;
import com.haulmont.testtask.models.RoleType;
import com.haulmont.testtask.models.entities.Recipe;
import com.haulmont.testtask.models.entities.Role;
import com.haulmont.testtask.models.entities.User;
import com.haulmont.testtask.repositories.RecipeRepository;
import com.haulmont.testtask.repositories.RoleRepository;
import com.haulmont.testtask.repositories.UserRepository;
import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.haulmont.testtask.beans",
        "com.haulmont.testtask.UI.views"
})
@EnableJpaRepositories(basePackages = {
        "com.haulmont.testtask.repositories"
})
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Theme("valo")
    @SpringUI(path = "")
    public static class MainNavigator extends UI {

        @Autowired
        private MainView mainView;
        @Autowired
        private DoctorsView doctorsView;
        @Autowired
        private PatientsView patientsView;
        @Autowired
        private RecipesView recipesView;

        private List<RouteLink> links;
        private Navigator navigator;

        private VerticalLayout mainLayout = new VerticalLayout();
        private Panel header = new Panel();
        private Panel container = new Panel();

        @Override
        protected void init(VaadinRequest vaadinRequest) {
            links = getLinks();
            navigator = getNavigator(links, new ErrorView(), container);
            header.setContent(new HeaderComponent(links, navigator));
            mainLayout.addComponent(header);
            mainLayout.addComponent(container);
            this.setContent(mainLayout);
        }

        private List<RouteLink> getLinks() {
            List<RouteLink> links = new ArrayList<>();
            links.add(new RouteLink("", "Main", mainView));
            links.add(new RouteLink("doctors", "Doctors", doctorsView));
            links.add(new RouteLink("patients", "Patients", patientsView));
            links.add(new RouteLink("recipes", "Recipes", recipesView));
            return links;
        }

        private Navigator getNavigator(List<RouteLink> views, View errorView, SingleComponentContainer container) {
            Navigator navigator = new Navigator(this, container);
            for (RouteLink link : views) {
                navigator.addView(link.getUrl(), link.getComponent());
            }
            navigator.setErrorView(errorView);
            return navigator;
        }
    }

    @Bean
    public CommandLineRunner loadData(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RecipeRepository recipeRepository) {
        return (args) -> {
            Role doctor = new Role(RoleType.DOCTOR, "crew of the hospital");
            Role patient = new Role(RoleType.PATIENT, "client of the hospital");
            User u1patient = new User(
                    "fntest", "mntest", "sntest", "phone", patient
            );
            User u2patient = new User(
                    "fntest2", "mntest2", "sntest2", "p1hone", patient
            );
            User u1doctor = new User(
                    "doc", "doc", "doc", "phone123", "Therapist", doctor
            );
            User u2doctor = new User(
                    "doc2", "doc2", "doc2", "phone321", "Psycho", doctor
            );
            Recipe r1 = new Recipe(
                    "Recipe 1", "Brash teeth", RecipePriority.STATIM, u1doctor
            );
            Recipe r2 = new Recipe(
                    "Recipe 2", "Wash hands", RecipePriority.CITO, u2doctor
            );
            Recipe r3 = new Recipe(
                    "Recipe 3", "Shower", RecipePriority.STANDARD, u2doctor
            );
            roleRepository.save(doctor);
            roleRepository.save(patient);
            userRepository.save(u1patient);
            userRepository.save(u2patient);
            userRepository.save(u1doctor);
            userRepository.save(u2doctor);
            recipeRepository.save(r1);
            recipeRepository.save(r2);
            recipeRepository.save(r3);

            System.out.println(userRepository.getAllUsersWithRole(RoleType.DOCTOR));
            log.info(roleRepository.findAll().toString());
            log.info(userRepository.findAll().toString());
            log.info(recipeRepository.findAll().toString());
            log.info("bean initialized");
            log.info("bean initialized2");
        };
    }
}
