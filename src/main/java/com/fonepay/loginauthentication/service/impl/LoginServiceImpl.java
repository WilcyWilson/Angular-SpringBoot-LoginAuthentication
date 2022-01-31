package com.fonepay.loginauthentication.service.impl;

import com.fonepay.loginauthentication.dto.GetDataDTO;
import com.fonepay.loginauthentication.dto.ResponseDTO;
import com.fonepay.loginauthentication.dto.UserLoginDTO;
import com.fonepay.loginauthentication.entity.UserLogin;
import com.fonepay.loginauthentication.repository.LoginRepo;
import com.fonepay.loginauthentication.service.EncryptionService;
import com.fonepay.loginauthentication.service.LoginService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    private LoginRepo loginRepository;

    @Autowired
    private EncryptionService encryptionService;

    private ResponseDTO responseDTO = new ResponseDTO();

    public ResponseEntity<ResponseDTO> checkUser(UserLoginDTO userLoginDTO) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException, InvalidKeyException {
        if(userLoginDTO.getUserName()!=null || !userLoginDTO.getUserName().isEmpty()){
            UserLogin userLogin=loginRepository.findByUserName(userLoginDTO.getUserName());
            if(userLogin != null) {
                if (encryptionService.decrypt(userLogin.getPassword(),userLogin.getUserName()).equals(userLoginDTO.getPassword())) {
                    responseDTO.setResponseStatus(true);
                    responseDTO.setResponseMessage("Username and password Correct. Login Successful. Welcome Kale Dai.");
                } else {
                    responseDTO.setResponseStatus(false);
                    responseDTO.setResponseMessage("Username or password Incorrect");
                }
            } else {
                responseDTO.setResponseStatus(false);
                responseDTO.setResponseMessage("Username not found");
            }
        }else{
            responseDTO.setResponseStatus(false);
            responseDTO.setResponseMessage("Username is empty or null");
        }
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    public ResponseEntity<Object> getData(int page, int size){
        if(loginRepository.findAll()!=null){
            List<GetDataDTO> getDataDTOList = new ArrayList<>();

            Pageable paging = PageRequest.of(page, size);
            Page<UserLogin> pageGetDataDTO = loginRepository.findAll(paging);

            List<UserLogin> userLoginList = pageGetDataDTO.getContent();

            for (UserLogin user: userLoginList){
                GetDataDTO getDataDTO = new GetDataDTO();

                getDataDTO.setUserName(user.getUserName());
                getDataDTO.setEmailId(user.getEmailId());
                getDataDTO.setId(user.getId());
                getDataDTO.setStatus(user.getStatus());
                getDataDTO.setCreatedBy(user.getCreatedBy());

                getDataDTOList.add(getDataDTO);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("logins", getDataDTOList);
            response.put("currentPage", pageGetDataDTO.getNumber());
            response.put("totalItems", pageGetDataDTO.getTotalElements());
            response.put("totalPages", pageGetDataDTO.getTotalPages());

            return new ResponseEntity<>(response,HttpStatus.OK);
        } else
        {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
