package com.raj.ecommerce.service;

import com.raj.ecommerce.domain.Address;
import com.raj.ecommerce.domain.User;
import com.raj.ecommerce.dto.AddressRequest;
import com.raj.ecommerce.exception.BadRequestException;
import com.raj.ecommerce.exception.RecordNotFoundException;
import com.raj.ecommerce.repo.AddressRepo;
import com.raj.ecommerce.repo.UserRepository;
import com.raj.ecommerce.util.DomainConverter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private AddressRepo addressRepo;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private DomainConverter domainConverter;

    public String onboardingAddress(@Valid AddressRequest request, Long userId) {
        try {
            Address addressInfo=domainConverter.addressDtoToAddress(request,userId);
            addressRepo.save(addressInfo);
            return "Address added successfully !";
        } catch (Exception ex) {
            throw new BadRequestException("Something Went Wrong "+ex);
        }
    }

    public User findByEmail(String name) {
        return userRepo.findByEmail(name).orElseThrow(()->new RecordNotFoundException("User not found!"));
    }
}
