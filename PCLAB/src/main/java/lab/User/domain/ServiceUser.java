package lab.User.domain;

import lab.User.infrest.UserRepository;
import lab.User.infrest.UserResponse;
import lab.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceUser {

    private final UserRepository userRepository;

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }
}
