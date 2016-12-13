package com.jof.springmvc.service;

import com.jof.springmvc.model.User;
import com.jof.springmvc.model.UserProfile;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Ferenc_S on 12/13/2016.
 */
public class MockUserService implements UserService {
    List<User> users;

    public MockUserService() {
        users = new ArrayList<>();
        User user;

        user = new User();
        user.setUsername("AdminName");
        user.setEmail("admin@f23fff.sem");
        user.setId(66);
        user.setPassword("pwd");
        UserProfile profile = new UserProfile();
        profile.setId(66);
        profile.setType("USER");
        profile.setType("ADMIN");
        user.setUserProfiles(new HashSet<UserProfile>(Arrays.asList(profile)));

        for (int i = 0; i < 5; i++) {
            user = new User();
            user.setUsername("Name " + i);
            user.setEmail("useremail" + i + "@f23fff.sem");
            user.setId(i);
            user.setPassword("pwd");
            profile = new UserProfile();
            profile.setId(i);
            profile.setType("USER");
            user.setUserProfiles(new HashSet<UserProfile>(Arrays.asList(profile)));
            users.add(new User());
        }
    }

    @Override
    public User findById(int id) {
        return users.get(id);
    }

    @Override
    public User findByUserName(final String username) {
        for (User user : users) {
            if (user.getUsername() != null && user.getUsername().equals(username))
                return user;
        }
        return null;
    }

    @Override
    public void saveUser(User user) {
        users.add(user);
    }

    @Override
    public void updateUser(User user) {
        users.set(users.indexOf(user), user);
    }

    @Override
    public void deleteUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername() != null && user.getUsername().equals(username))
                users.remove(user);
        }
    }

    @Override
    public List<User> findAllUsers() {
        return users;
    }

    @Override
    public boolean isUsernameUnique(Integer id, String username) {
        return findByUserName(username) == null;
    }
}
