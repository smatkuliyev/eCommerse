package com.lec.ecommerse.service;

import com.lec.ecommerse.domain.Role;
import com.lec.ecommerse.domain.User;
import com.lec.ecommerse.domain.enumeration.UserRole;
import com.lec.ecommerse.dto.AdminDTO;
import com.lec.ecommerse.dto.UserDTO;
import com.lec.ecommerse.email.EmailService;
import com.lec.ecommerse.exception.AuthException;
import com.lec.ecommerse.exception.BadRequestException;
import com.lec.ecommerse.exception.ConflictException;
import com.lec.ecommerse.exception.ResourceNotFoundException;
import com.lec.ecommerse.repository.RoleRepository;
import com.lec.ecommerse.repository.UserRepository;
import com.lec.ecommerse.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final static String USER_NOT_FOUND_MSG = "user with id %d not found";
    private final EmailService emailService;

    public List<UserDTO> fetchAllUsers(){
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();

        UserDTO userDTO = new UserDTO();

        for (User user: users) {
            userDTO.setRoles(user.getRoles());
            userDTOList.add(new UserDTO(user.getFirstName(), user.getLastName(),
                    user.getPhoneNumber(), user.getEmail(), user.getAddress(),
                    user.getZipCode(), user.getBuiltIn(), user.getEnabled(), userDTO.getRoles()));
        }

        return userDTOList;
    }

    public UserDTO findById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

        UserDTO userDTO = new UserDTO();
        userDTO.setRoles(user.getRoles());

        return new UserDTO(user.getFirstName(), user.getLastName(),
                user.getPhoneNumber(), user.getEmail(), user.getAddress(),
                user.getZipCode(), user.getBuiltIn(), user.getEnabled(), userDTO.getRoles());
    }

    public void register(User user) throws BadRequestException {

        if (userRepository.existsByEmail(user.getEmail())) {
            User user1 = userRepository.findByEmail(user.getEmail()).get();
            if (user1.getEnabled()) {
                throw new ConflictException("Error: Email is already in use!");
            }
        }

        if (!userRepository.existsByEmail(user.getEmail())) {

            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            Set<Role> roles = new HashSet<>();
            Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));

            roles.add(customerRole);
            user.setRoles(roles);

            userRepository.save(user);
        }

        String token = UUID.randomUUID().toString();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
        String currentTime = dateTimeFormatter.format(LocalDateTime.now());
        String link = "http://localhost:8080/ecommerse/api/confirm?token=" + token + "" +
                "&id=" + user.getId() + "&date=" + currentTime;

        if (userRepository.existsByEmail(user.getEmail()) && !user.getEnabled()) {
            emailService.send(user.getEmail(), buildEmail(user.getFirstName(), link));
        }
    }

    public void confirmUser(String token, String id, String date) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
        LocalDateTime parseTime = LocalDateTime.parse(date, dateTimeFormatter);

        if (parseTime.isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new DateTimeException("Token has expired");
        }
        if (!userRepository.existsById(Long.valueOf(id))) {
            throw new IllegalStateException("Invalid credentials!");
        }

        User user = userRepository.findById(Long.valueOf(id)).get();
        user.setEnabled(true);
        userRepository.save(user);

    }

    public void login(String email, String password) throws AuthException {
        try {

            Optional<User> user = userRepository.findByEmail(email);

            if (!BCrypt.checkpw(password, user.get().getPassword())) { //girilen password ile sistemdeki kontrol eder
                throw new AuthException("Invalid credentials");
            }

        } catch (Exception e) {
            throw new AuthException("Invalid credentials");
        }
    }

    public void updateUser(Long id, UserDTO userDTO) throws BadRequestException {
        boolean emailExists = userRepository.existsByEmail(userDTO.getEmail());
        Optional<User> userDetails = userRepository.findById(id);
        if (userDetails.get().getBuiltIn()) {
            throw new BadRequestException("You dont have permission to update user info!");
        }
        if (emailExists && !userDTO.getEmail().equals(userDetails.get().getEmail())) {
            throw new ConflictException("Error: Email is already in use!");
        }
        userRepository.update(id, userDTO.getFirstName(), userDTO.getLastName(), userDTO.getPhoneNumber(),
                userDTO.getEmail(), userDTO.getAddress(), userDTO.getZipCode());
    }

    public void updatePassword(Long id, String newPassword, String oldPassword) throws BadRequestException {
        Optional<User> user = userRepository.findById(id);
        if (user.get().getBuiltIn()) {
            throw new BadRequestException("You dont have permission to update password!");
        }

        if (!BCrypt.hashpw(oldPassword, user.get().getPassword()).equals(user.get().getPassword()))
            throw new BadRequestException("password does not match");

        String hashedPassword = passwordEncoder.encode(newPassword);
        user.get().setPassword(hashedPassword);

        userRepository.save(user.get());
    }

    public void updateUserAuth(Long id, AdminDTO adminDTO) throws BadRequestException {
        boolean emailExist = userRepository.existsByEmail(adminDTO.getEmail());
        Optional<User> userDetails = userRepository.findById(id);

        if (userDetails.get().getBuiltIn()) {
            throw new BadRequestException("You dont have permission to update user info!");
        }
        adminDTO.setBuiltIn(false);

        if (emailExist && !adminDTO.getEmail().equals(userDetails.get().getEmail())) {
            throw new ConflictException("Error: Email is already in use!");
        }

        if (adminDTO.getPassword() == null) {
            adminDTO.setPassword(userDetails.get().getPassword());
        } else {
            String encodedPassword = passwordEncoder.encode(adminDTO.getPassword());
            adminDTO.setPassword(encodedPassword);
        }

        Set<String> userRoles = adminDTO.getRoles();
        Set<Role> roles = addRoles(userRoles);

        User user = new User(id, adminDTO.getFirstName(), adminDTO.getLastName(), adminDTO.getPassword(), adminDTO.getPhoneNumber(),
                adminDTO.getEmail(), adminDTO.getAddress(), adminDTO.getZipCode(), adminDTO.getBuiltIn(), adminDTO.getEnabled(), roles);
        userRepository.save(user);
    }

    public void removeById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException(String.format(USER_NOT_FOUND_MSG, id)));

        if (user.getBuiltIn()){
            throw new BadRequestException("You dont have permission to delete user !");
        }
        userRepository.deleteById(id);
    }

    public Set<Role> addRoles(Set<String> userRoles) {

        Set<Role> roles = new HashSet<>();

        if (userRoles == null) {
            Role userRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            userRoles.forEach(role -> {
                switch (role) {
                    case "Administrator":
                        Role adminRole = roleRepository.findByName(UserRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        return roles;
    }

    //mail
    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 5 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
}
