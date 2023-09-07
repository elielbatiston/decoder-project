package com.ead.authuser.controller;

import com.ead.authuser.configs.security.JwtProvider;
import com.ead.authuser.dtos.JwtDto;
import com.ead.authuser.dtos.LoginDto;
import com.ead.authuser.dtos.UserDto;
import com.ead.authuser.enums.RoleType;
import com.ead.authuser.enums.UserStatus;
import com.ead.authuser.enums.UserType;
import com.ead.authuser.models.RoleModel;
import com.ead.authuser.models.UserModel;
import com.ead.authuser.services.RoleService;
import com.ead.authuser.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import com.netflix.discovery.converters.Auto;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("auth")
public class AuthenticationController {

    //private final Logger logger = LogManager.getLogger(AuthenticationController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("signup")
    public ResponseEntity<Object> registerUser(
        @RequestBody
        @Validated(UserDto.UserView.RegistrationPost.class)
        @JsonView(UserDto.UserView.RegistrationPost.class)
        UserDto dto
    ) {
        log.debug("POST registerUser userDto received {} ", dto.toString());
        if (userService.existsByUsername(dto.getUserName())) {
            log.warn("Username {} is already taken", dto.toString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: Username is already taken!");
        }
        if (userService.existsByEmail(dto.getEmail())) {
            log.warn("Email {} is already taken", dto.toString());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: E-mail is already taken!");
        }
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        final RoleModel roleModel = roleService.findByRoleName(RoleType.ROLE_STUDENT)
                .orElseThrow(() -> new RuntimeException("Error: Role is Not Found."));
        var model = new UserModel();
        BeanUtils.copyProperties(dto, model);
        model.setUserStatus(UserStatus.ACTIVE);
        model.setUserType(UserType.STUDENT);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.getRoles().add(roleModel);
        userService.saveUser(model);
        log.debug("POST registerUser userId saved {} ", model.getUserId());
        log.info("User saved successfully userId {} ", model.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(model);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        final Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String jwt = jwtProvider.generateJwt(authentication);
        return ResponseEntity.ok(new JwtDto(jwt));
    }

    @GetMapping
    public String index() {
        /*
            Utilizamos esse tipo de log quando queremos a visualização de forma mais granular, com uma granularidade mais fina
            Buscando um rastreamento de alguma parte do codigo. Ele vai trazer o log muito detalhado
         */
        log.trace("TRACE");

        /*
            Utilizamos principalmente em ambiente de dev para trazer informações relevantes aos devs pra quem estiver atuando na
            implementação do codigo. Quando precisa ver um valor de uma determinada variavel, conseguimos ver um detalhamento melhor
            quando um metodo é chamado
         */
        log.debug("DEBUG");

        /*
            Traz informações uteis e relevantes mas de fluxos que aconteceram normalmente
         */
        log.info("INFO");

        /*
            Não é um erro, é um alerta.
            Em determinados processamentos qdo teve uma perda de dados secundarios ou quando um processamento foi feito mais de uma vez
         */
        log.warn("WARN");

        /*
            Insere erro e detalhe ele. Utilizado em try catch
         */
        log.error("ERROR");

        return "Logging Spring Boot...";
    }
}
