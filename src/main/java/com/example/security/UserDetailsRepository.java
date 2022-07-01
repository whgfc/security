package com.example.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : demo
 * @description :
 */
public class UserDetailsRepository {

    protected final Log logger = LogFactory.getLog(getClass());

    private Map<String, UserDetails> userDetailsMap = new HashMap<>();

    public void createUser(UserDetails user) {
        this.userDetailsMap.putIfAbsent(user.getUsername(), user);
    }

    public void updateUser(UserDetails user) {
        this.userDetailsMap.put(user.getUsername(), user);
    }

    public void deleteUser(String username) {
        this.userDetailsMap.remove(username);
    }

    public void changePassword(String oldPassword, String newPassword) {
        Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
        if (currentUser == null) {
            // This would indicate bad coding somewhere
            throw new AccessDeniedException(
                    "Can't change password as no Authentication object found in context " + "for current user.");
        }
        String username = currentUser.getName();
        this.logger.debug(LogMessage.format("Changing password for user '%s'", username));
        UserDetails userDetails = this.userDetailsMap.get(username);
        if (null == userDetails) {
            throw new IllegalStateException("Current user doesn't exist in database.");
        }
        // todo 参考 InMemoryUserDetailsManager 自己实现
    }

    public boolean userExists(String username) {
        return this.userDetailsMap.containsKey(username);
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.userDetailsMap.get(username);
    }
}
