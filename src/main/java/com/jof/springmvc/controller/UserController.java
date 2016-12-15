package com.jof.springmvc.controller;

import com.jof.springmvc.model.Role;
import com.jof.springmvc.model.User;
import com.jof.springmvc.service.RiotApiService;
import com.jof.springmvc.service.RoleService;
import com.jof.springmvc.service.UserService;
import net.rithms.riot.api.RiotApiException;
import net.rithms.riot.constant.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;


@Controller
@RequestMapping("/")
@SessionAttributes("roles")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RiotApiService riotApiService;

    @Autowired
    RoleService roleService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;

    @Autowired
    AuthenticationTrustResolver authenticationTrustResolver;


    /**
     * This method will list all existing users.
     */
    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String reviews(ModelMap model, HttpServletRequest request) {
        if (request.getSession().getAttribute("remoteUser") == null && getPrincipal() != null) {
            User user = userService.findByUserName(getPrincipal());
//            if(!user.getRegion().equals(request.getSession().getAttribute("region")) {
//            	user.setRegion(request.getSession().getAttribute("region"));
//            }
            request.getSession().setAttribute("remoteUser", user);
        }
        //TODO GET REVIEWS
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "reviewList";
    }

    /**
     * This method will list all existing users.
     */
    @RequestMapping(value = {"/admin/list"}, method = RequestMethod.GET)
    public String listUsers(ModelMap model, HttpServletRequest request) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "userlist";
    }

    /**
     * This method will provide the medium to add a new user.
     */
    @RequestMapping(value = {"/registration"}, method = RequestMethod.GET)
    public String newUser(ModelMap model, HttpServletRequest request) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("edit", false);
        return "registration";
    }

    /**
     * This method will be called on form submission, handling POST request for
     * registering a new user in the database. It also validates the user input
     */
    @RequestMapping(value = {"/registration"}, method = RequestMethod.POST)
    public String registerUser(@Valid User user, BindingResult result,
                               ModelMap model) {
        Region region = Region.EUNE;
        //   validateSummonerRunePage(region, result, user.getUsername());

        //TODO: get ID from API HERE
        user.setId(Long.valueOf(new Random().nextInt()));
        if (result.hasErrors()) {
            return "registration";
        }

        if (user.getRoles().isEmpty()) {
            Set<Role> profiles = new HashSet<Role>();
            profiles.add(roleService.findByType("USER"));
            user.setRoles(profiles);
        }

        if (!userService.isUsernameUnique(user.getId(), user.getUsername())) {
            FieldError usernameError = new FieldError("user", "username", messageSource.getMessage("non.unique.username", new String[]{user.getUsername()}, Locale.getDefault()));
            result.addError(usernameError);
            return "registration";
        }
        userService.saveUser(user);

        model.addAttribute("success", "User " + user.getUsername() + " registered successfully");
        //return "success";
        return "login";
    }


    /**
     * This method will provide the medium for users to be registered
     */
    @RequestMapping(value = {"/admin/newuser"}, method = RequestMethod.GET)
    public String registerNewUser(ModelMap model, HttpServletRequest request) {
        User user = new User();
        // generate user for testing
//        String s = UUID.randomUUID().toString();
//        user.setUsername(s);
//        user.setPassword("1234");
//        user.setEmail(s + "@" + s + ".com");
        model.addAttribute("user", user);
        model.addAttribute("edit", false);

        return "newUser";
    }

    /**
     * This method will be called on form submission, handling POST request for
     * saving user in database. It also validates the user input
     */
    @RequestMapping(value = {"/admin/newuser"}, method = RequestMethod.POST)
    public String saveUser(@Valid User user, BindingResult result,
                           ModelMap model) {
        Region region = Region.EUNE;
        validateSummonerRunePage(region, result, user.getUsername());
        if (result.hasErrors()) {
            return "newUser";
        }

        if (user.getRoles().isEmpty()) {
            Set<Role> profiles = new HashSet<Role>();
            profiles.add(roleService.findByType("USER"));
            user.setRoles(profiles);
        }

        if (!userService.isUsernameUnique(user.getId(), user.getUsername())) {
            FieldError usernameError = new FieldError("user", "username", messageSource.getMessage("non.unique.username", new String[]{user.getUsername()}, Locale.getDefault()));
            result.addError(usernameError);
            return "newUser";
        }
        userService.saveUser(user);

        model.addAttribute("success", "User " + user.getUsername() + " registered successfully");
        //return "success";
        return "registrationSuccess";
    }


    /**
     * This method will provide the medium to update an existing user.
     */
    @RequestMapping(value = {"/admin/edit-user-{username}"}, method = RequestMethod.GET)
    public String editUser(@PathVariable String username, ModelMap model) {
        User user = userService.findByUserName(username);
        model.addAttribute("user", user);
        model.addAttribute("edit", true);
        return "registration";
    }

    /**
     * This method will be called on form submission, handling POST request for
     * updating user in database. It also validates the user input
     */
    @RequestMapping(value = {"/admin/edit-user-{username}"}, method = RequestMethod.POST)
    public String updateUser(@Valid User user, BindingResult result,
                             ModelMap model, @PathVariable String username) {

        if (result.hasErrors()) {
            return "registration";
        }

        /*//Uncomment below 'if block' if you WANT TO ALLOW UPDATING SSO_ID in UI which is a unique key to a User.
        if(!userService.isUsernameUnique(user.getId(), user.getUsername())){
            FieldError ssoError =new FieldError("user","username",messageSource.getMessage("non.unique.username", new String[]{user.getUsername()}, Locale.getDefault()));
            result.addError(ssoError);
            return "registration";
        }*/


        userService.updateUser(user);

        model.addAttribute("success", "User " + user.getUsername() + " updated successfully");
        return "registrationSuccess";
    }


    /**
     * This method will delete an user by it's username value.
     */
    @RequestMapping(value = {"/admin/delete-user-{username}"}, method = RequestMethod.GET)
    public String deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return "redirect:/list";
    }


    /**
     * This method will provide Role list to views
     */
    @ModelAttribute("roles")
    public List<Role> initializeProfiles() {
        return roleService.findAll();
    }

    /**
     * This method handles Access-Denied redirect.
     */
    @RequestMapping(value = "/Access_Denied", method = RequestMethod.GET)
    public String accessDeniedPage(ModelMap model) {
        return "accessDenied";
    }

    /**
     * This method handles login GET requests.
     * If users is already logged-in and tries to goto login page again, will be redirected to list page.
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        if (isCurrentAuthenticationAnonymous()) {
            return "login";
        } else {
            return "redirect:/";
        }
    }

    /**
     * This method handles logout requests.
     * Toggle the handlers if you are RememberMe functionality is useless in your app.
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            //new SecurityContextLogoutHandler().logout(request, response, auth);
            persistentTokenBasedRememberMeServices.logout(request, response, auth);
            SecurityContextHolder.getContext().setAuthentication(null);
            request.getSession().setAttribute("remoteUser", null);
        }
        return "redirect:/login?logout";
    }

    @RequestMapping(value = "/user/matchhistory", method = RequestMethod.GET)
    public String matchHistory(HttpServletRequest request, HttpServletResponse response) {
        try {
            User remoteUser = (User) request.getSession().getAttribute("remoteUser");
            request.setAttribute("games", riotApiService.getRecentGames(Region.EUNE, remoteUser.getId()));
        } catch (RiotApiException e) {
            e.printStackTrace();
        }
        return "matchHistory";
    }

    /**
     * This method returns the principal[user-name] of logged-in user.
     */
    private String getPrincipal() {
        String userName = null;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        return userName;
    }

    /**
     * This method returns true if users is already authenticated [logged-in], else false.
     */
    private boolean isCurrentAuthenticationAnonymous() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authenticationTrustResolver.isAnonymous(authentication);
    }

    /**
     * This methods checks if the summoner has a runepage called "RYP"
     */

    private void validateSummonerRunePage(Region region, BindingResult result, String username) {
        try {
            long l = riotApiService.getSummonerIdByName(Region.EUNE, username);
            if (riotApiService.userHasRunePage(Region.EUNE, l, "RYP") == false) {
                result.addError(new ObjectError("User", "user.validation.noMatchingRunePage"));
            }
        } catch (RiotApiException e) {
            result.addError(new ObjectError("User", "user.validation.noMatchingSummoner"));
        }
    }


}
