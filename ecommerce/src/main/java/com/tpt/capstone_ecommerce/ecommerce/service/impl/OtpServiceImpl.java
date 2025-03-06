package com.tpt.capstone_ecommerce.ecommerce.service.impl;

import com.tpt.capstone_ecommerce.ecommerce.entity.Otp;
import com.tpt.capstone_ecommerce.ecommerce.enums.OTP_STATUS;
import com.tpt.capstone_ecommerce.ecommerce.exception.NotFoundException;
import com.tpt.capstone_ecommerce.ecommerce.exception.TimeExpiredException;
import com.tpt.capstone_ecommerce.ecommerce.repository.OtpRepository;
import com.tpt.capstone_ecommerce.ecommerce.service.OtpService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class OtpServiceImpl implements OtpService {
    private final OtpRepository otpRepository;

    public OtpServiceImpl(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    @Override
    public boolean validateOtp(String otp) {
        Otp findOtp = this.otpRepository.findByOtp(otp);
        if (findOtp == null) {
            throw new NotFoundException("Otp not found");
        }

        if(findOtp.getExpiredAt().isBefore(
                LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())
        )) {
            throw new TimeExpiredException("Otp is expired");
        }

        findOtp.setStatus(OTP_STATUS.VERIFIED);
        this.otpRepository.save(findOtp);

        return true;
    }

    @Override
    public String generateOtp(String userEmail) {
        Otp otp = new Otp();
        otp.setUserEmail(userEmail);
        otp.setStatus(OTP_STATUS.PENDING);
        otp.setExpiredAt(LocalDateTime.now().plusMinutes(5));
        otp.setOtp(UUID.randomUUID().toString());
        Otp saved = this.otpRepository.save(otp);
        return saved.getOtp();
    }
}
