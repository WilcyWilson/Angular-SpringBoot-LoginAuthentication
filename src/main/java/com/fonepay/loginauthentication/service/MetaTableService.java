package com.fonepay.loginauthentication.service;

import com.fonepay.loginauthentication.dto.MetaTableDTO;
import com.fonepay.loginauthentication.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface MetaTableService {

    //    String metaTableVale (String name) throws Exception;
    ResponseEntity<ResponseDTO> insertMeta(MetaTableDTO metaTableDTO);
}
