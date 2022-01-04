package com.fonepay.loginauthentication.service;

import com.fonepay.loginauthentication.dto.ResponseDTO;
import com.fonepay.loginauthentication.dto.UserLoginDTO;
import com.fonepay.loginauthentication.entity.UserLogin;
import com.fonepay.loginauthentication.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Service
public class LoginServiceImpl implements LoginService{
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private RegistrationService registrationService;

    private ResponseDTO responseDTO = new ResponseDTO();

    public ResponseEntity<ResponseDTO> checkUser(UserLoginDTO userLoginDTO) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        if(userLoginDTO.getUserName()!=null || !userLoginDTO.getUserName().isEmpty()){
            UserLogin userLogin=loginRepository.findByUserName(userLoginDTO.getUserName());

            SecretKey key = EncryptionServiceImpl.generateKey(userLogin.getEmailId(), userLogin.getUserName());
            IvParameterSpec ivParameterSpec = EncryptionServiceImpl.generateIv();
            String algorithm = "AES/CBC/PKCS5Padding";
            String plainText = EncryptionServiceImpl.decrypt(algorithm, userLogin.getPassword(), key, ivParameterSpec);

            if(plainText.equals(userLoginDTO.getPassword())){
                responseDTO.setResponseStatus(true);
                responseDTO.setResponseMessage("Username and password Correct. Login Successful");

            }else{
                responseDTO.setResponseStatus(false);
                responseDTO.setResponseMessage("Username or password Incorrect");
            }

        }else{
            responseDTO.setResponseStatus(false);
            responseDTO.setResponseMessage("Username is empty or null");
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }


}