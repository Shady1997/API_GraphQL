package org.example.graphqlcrudapi.config;

import org.example.graphqlcrudapi.entity.User;
import org.example.graphqlcrudapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;

    @Autowired
    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            loadInitialData();
        }
    }

    private void loadInitialData() {
        List<User> users = Arrays.asList(
                new User("John Doe", "john.doe@example.com", "+1234567890", "123 Main St, New York, NY"),
                new User("Jane Smith", "jane.smith@example.com", "+1234567891", "456 Oak Ave, Los Angeles, CA"),
                new User("Bob Johnson", "bob.johnson@example.com", "+1234567892", "789 Pine Rd, Chicago, IL"),
                new User("Alice Brown", "alice.brown@example.com", "+1234567893", "321 Elm St, Houston, TX"),
                new User("Charlie Wilson", "charlie.wilson@example.com", "+1234567894", "654 Maple Dr, Phoenix, AZ"),
                new User("Diana Davis", "diana.davis@example.com", "+1234567895", "987 Cedar Ln, Philadelphia, PA"),
                new User("Edward Miller", "edward.miller@example.com", "+1234567896", "147 Birch St, San Antonio, TX"),
                new User("Fiona Garcia", "fiona.garcia@example.com", "+1234567897", "258 Spruce Ave, San Diego, CA"),
                new User("George Martinez", "george.martinez@example.com", "+1234567898", "369 Willow Way, Dallas, TX"),
                new User("Helen Rodriguez", "helen.rodriguez@example.com", "+1234567899", "741 Poplar Pl, San Jose, CA")
        );

        userRepository.saveAll(users);
        System.out.println("Loaded " + users.size() + " initial users into the database.");
    }
}