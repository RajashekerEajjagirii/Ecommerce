package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.primary.Role;
import com.raj.ecommerce.domain.primary.User;
import com.raj.ecommerce.dto.LogInRequest;
import com.raj.ecommerce.dto.LogInResponse;
import com.raj.ecommerce.dto.RegisterRequest;
import com.raj.ecommerce.exception.BadRequestException;
import com.raj.ecommerce.exception.RecordAlreadyExistsException;
import com.raj.ecommerce.repo.primary.UserRepository;
import com.raj.ecommerce.security.JwtTokenProvider;
import com.raj.ecommerce.security.UserInfoDetailsService;
import com.raj.ecommerce.util.DomainConverter;
import com.raj.ecommerce.util.EmailBodyBuildTemplate;
import com.raj.ecommerce.util.NumberGenerator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserInfoDetailsService userInfoDetailsService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private NumberGenerator numberGenerator;
    @Autowired
    private MailService mailService;
    @Autowired
    private EmailBodyBuildTemplate emailTemplate;
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.expiration-ms}")
    private long validityInMs;

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final DomainConverter domainConverter;

    public AuthService(UserRepository userRepository, DomainConverter domainConverter){
        this.userRepo=userRepository;
        this.bCryptPasswordEncoder=new BCryptPasswordEncoder();
        this.domainConverter=domainConverter;
    }

    public String register(@Valid RegisterRequest request) {
        if(userRepo.existsByEmail(request.getEmail())){
            throw new RecordAlreadyExistsException("Your already registered with Us, Please singIn.");
        }
        try {
            User userInfo = domainConverter.registerDtoToUser(request, bCryptPasswordEncoder.encode(request.getPassword()));
            String emailOtp=numberGenerator.generate4DigitNumber();
            userInfo.setOtp(Integer.parseInt(emailOtp));
            mailService.sendHtmlEmail(request.getEmail(),"Account Verification-Raj eCommerce",
                    emailTemplate.accountVerifyTemplate(request.getUsername(),emailOtp));
            userRepo.save(userInfo);
             return "Your Successfully register with Us!";

        } catch (Exception ex) {
            throw new BadRequestException("Service unavailable,give a try later! "+ex);
        }

    }

    public LogInResponse login(@Valid LogInRequest request) {
        try{

            /*
             * Validating with db
             */
            User userInfo=userInfoDetailsService.findByEmail(request.getEmail());
            if(!bCryptPasswordEncoder.matches(request.getPassword(),userInfo.getPassword())){
               throw new BadRequestException("Invalid Password! ");
            }
            //Generating token
            String token=tokenProvider.createToken(userInfo.getEmail(),userInfo.getRoles().stream().map(Role::getName).toList());
            return new LogInResponse("Bearer",token, validityInMs/60000+" minutes");
        } catch (Exception ex) {
            throw new BadRequestException("Service unavailable,give a try later! "+ex);
        }

    }

    public String verifyAccount(int emailOtp) {
        try{
           Optional<User> user=userRepository.findByOtp(emailOtp);
           if(user.isPresent()){
               user.get().setVerified(true);
               userRepo.save(user.get());
           }
           return "Your account was verified successfully!";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
