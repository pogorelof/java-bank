package kz.pgrlv.springbootpractise.services;

import kz.pgrlv.springbootpractise.persistence.UserDetailsImpl;
import kz.pgrlv.springbootpractise.persistence.dto.UserDto;
import kz.pgrlv.springbootpractise.persistence.entity.User;
import kz.pgrlv.springbootpractise.persistence.repository.AccountRepository;
import kz.pgrlv.springbootpractise.persistence.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountService accountService;

    public List<UserDto> getUsers(){
        List<User> users = userRepository.findAll();
        return users.stream().map(this::toDto).toList();
    }

    public UserDto getUserById(Integer id){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()){
            return this.toDto(userOptional.get());
        }
        return null;
    }

    public User getUserByUsername(String username){
        return userRepository.findUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public User saveUser(User user){
        User saved = userRepository.save(user);
        accountService.openAccount(saved);
        return saved;
    }

    public void deleteUser(Integer id){
        userRepository.deleteById(id);
    }

    public UserDto updateUser(Integer id, User user){
        User gettedUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        gettedUser.setUsername(user.getUsername());
        gettedUser.setPassword(user.getPassword());
        gettedUser.setEmail(user.getEmail());

        return toDto(userRepository.save(gettedUser));
    }

    public UserDto findByEmail(String email){
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isPresent()){
            return this.toDto(userOptional.get());
        }
        return null;
    }

    public UserDto getUserByPhoneNumber(String phone){
        Optional<User> userOptional = userRepository.findUserByPhoneNumber(phone);
        if (userOptional.isPresent()){
            return this.toDto(userOptional.get());
        }
        return null;
    }

    private UserDto toDto(User user){
        return new UserDto(user.getUsername(), user.getDateOfBirth(), user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found")
        );
        return UserDetailsImpl.build(user);
    }
}
