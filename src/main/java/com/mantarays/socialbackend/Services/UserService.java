package com.mantarays.socialbackend.Services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.transaction.Transactional;

import com.mantarays.socialbackend.Models.Role;
import com.mantarays.socialbackend.Models.User;
import com.mantarays.socialbackend.Repositories.RoleRepository;
import com.mantarays.socialbackend.Repositories.UserRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements UserServiceIntf, UserDetailsService
{
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User user)
    {
        log.debug("Saving new user \"{}\" to database.", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public User saveUser(User user)
    {
        log.debug("Saving new user \"{}\" to database.", user.getUsername());
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) 
    {
        log.debug("Saving new role \"{}\" to database.", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) 
    {
        User user = userRepo.findByUsername(username);
        Role role = roleRepo.findByName(roleName);

        log.debug("Saving new role \"{}\" to user \"{}\".", role.getName(), user.getUsername());

        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) 
    {
        User user = userRepo.findByUsername(username);
        log.debug("Returning user \"{}\".", user.getUsername());
        return user;
    }

    @Override
    public List<User> getUsers() 
    {
        log.error("Returning all users. PLEASE DONT USE THIS");
        return userRepo.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        User user = userRepo.findByUsername(username);
        if(user == null)
        {
            String error = "User not found in database.";
            log.error(error);
            throw new UsernameNotFoundException(error);
        }
        else
        {
            log.info("User {} found in database", username);
        }
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> 
        {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public void deleteAll()
    {
        userRepo.deleteAll();
    }
    
}
