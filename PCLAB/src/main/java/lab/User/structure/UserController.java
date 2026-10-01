package lab.User.structure;

import lab.User.domain.ServiceUser;
import lab.User.infrest.UserResponse;
import lab.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final ServiceUser serviceUser;

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal UserPrincipal current) {
        return serviceUser.findById(current.id());
    }
}
