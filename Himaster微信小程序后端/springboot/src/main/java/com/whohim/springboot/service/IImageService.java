package com.whohim.springboot.service;



import com.whohim.springboot.common.ServerResponse;


import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;

public interface IImageService {
    
    ServerResponse adminUpload(MultipartFile file,String token,String openid) throws IOException;

    ServerResponse savaAdminFace(String openid) throws IOException;

    ServerResponse faceContrast(String openid,String token,MultipartFile file) throws IOException;
}
