package project.finalproject1backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import project.finalproject1backend.domain.User;
import project.finalproject1backend.domain.UserRole;
import project.finalproject1backend.dto.ErrorDTO;
import project.finalproject1backend.dto.PrincipalDTO;
import project.finalproject1backend.dto.ResponseDTO;
import project.finalproject1backend.dto.ModifyRequestDTO;
import project.finalproject1backend.dto.user.UserLoginRequestDTO;
import project.finalproject1backend.dto.user.UserLoginResponseDTO;
import project.finalproject1backend.dto.user.UserModifyLicenseRequestDTO;
import project.finalproject1backend.dto.user.UserSignUpRequestDTO;
import project.finalproject1backend.jwt.JwtTokenProvider;
import project.finalproject1backend.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<?> signUp(UserSignUpRequestDTO requestDTO){
        if(userRepository.existsByUserId(requestDTO.getUserId())) {
            return new ResponseEntity<>(new ErrorDTO("400","existId"), HttpStatus.BAD_REQUEST);
        }
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_STANDBY);
        User user = User.builder()
                .userId(requestDTO.getUserId())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .ownerName(requestDTO.getOwnerName())
                .corporateNumber(requestDTO.getCorporateNumber())
                .openingDate(requestDTO.getOpeningDate())
                .businessLicense(requestDTO.getBusinessLicense())
                .managerName(requestDTO.getManagerName())
                .email(requestDTO.getEmail())
                .phoneNumber(requestDTO.getPhoneNumber())
                .role(roles)
                .build();
        userRepository.save(user);
        return new ResponseEntity<>(new ResponseDTO("200","success"), HttpStatus.OK);
    }

    public ResponseEntity<?> login(UserLoginRequestDTO requestDTO) {
        if(!userRepository.existsByUserId(requestDTO.getUserId())) {
            return new ResponseEntity<>(new ErrorDTO("400","checkId"), HttpStatus.BAD_REQUEST);
        }
        Optional<User> user = userRepository.findByUserId(requestDTO.getUserId());
        if(!passwordEncoder.matches(requestDTO.getPassword(),user.get().getPassword() )){
            return new ResponseEntity<>(new ErrorDTO("400","checkPassword"), HttpStatus.BAD_REQUEST);
        }
        String token = jwtTokenProvider.createToken(user.get().getUserId());
        return new ResponseEntity<>(new UserLoginResponseDTO("200",token),HttpStatus.OK);
    }

    public ResponseEntity<?> delete(PrincipalDTO principal) {
        userRepository.deleteById(principal.getId());
        return new ResponseEntity<>(new ResponseDTO("200","success"), HttpStatus.OK);
    }

    public ResponseEntity<?> modify(PrincipalDTO principal, ModifyRequestDTO modifyRequestDTO) {
        if(modifyRequestDTO.content==null){
            return new ResponseEntity<>(new ErrorDTO("400","checkContent"), HttpStatus.BAD_REQUEST);
        }
        String[] contents = {"password","phoneNumber","managerName","email"};
        int count =0;
        for (String s:contents) {
            if(s.equals(modifyRequestDTO.content)){
                count++;
            }
        }
        if(count==0){
            return new ResponseEntity<>(new ErrorDTO("400","checkContent"), HttpStatus.BAD_REQUEST);
        }
        Optional<User> user=userRepository.findById(principal.getId());
        switch (modifyRequestDTO.content){
            case "password":
                user.get().setPassword(passwordEncoder.encode(modifyRequestDTO.value));
                break;
            case "phoneNumber":
                user.get().setPhoneNumber(modifyRequestDTO.value);
                break;
            case "managerName":
                user.get().setManagerName(modifyRequestDTO.value);
                break;
            case "email":
                user.get().setEmail(modifyRequestDTO.value);
                break;
        }
        userRepository.save(user.get());
        return new ResponseEntity<>(modifyRequestDTO, HttpStatus.OK);
    }

    public ResponseEntity<?> modifyLicense(PrincipalDTO principal, UserModifyLicenseRequestDTO modifyRequestDTO) {
        if(!modifyRequestDTO.nullCheck()){
            return new ResponseEntity<>(new ErrorDTO("400","checkNull"), HttpStatus.BAD_REQUEST);
        }
        Optional<User> user=userRepository.findById(principal.getId());
        user.get().setOwnerName(modifyRequestDTO.getOwnerName());
        user.get().setOpeningDate(modifyRequestDTO.getOpeningDate());
        user.get().setCorporateNumber(modifyRequestDTO.getCorporateNumber());
        user.get().setBusinessLicense(modifyRequestDTO.getBusinessLicense());
        userRepository.save(user.get());
        return new ResponseEntity<>(new ResponseDTO("200","success"), HttpStatus.OK);
    }
}