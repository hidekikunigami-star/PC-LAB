package lab.User.domain;

import lab.User.DTO.AuthResponseDTO;
import lab.User.DTO.LoginRequestDTO;
import lab.User.DTO.RegisterRequestDTO;
import lab.User.infrest.UserRepository;
import lab.exception.ConflictException;
import lab.exception.ForbiddenException;
import lab.exception.UnauthorizedException;
import lab.security.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWT jwt;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (request.role() == Role.ROLE_ADMIN) {
            throw new ForbiddenException("No se puede registrar un ADMIN");
        }
        if (userRepository.existsByUsername(request.username()) || userRepository.existsByEmail(request.email())) {
            throw new ConflictException("El username o email ya existe");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role() == null ? Role.ROLE_CUSTOMER : request.role());
        userRepository.save(user);
        return new AuthResponseDTO(jwt.generateToken(user));
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new UnauthorizedException("Credenciales invalidas"));
        return new AuthResponseDTO(jwt.generateToken(user));
    }
}
