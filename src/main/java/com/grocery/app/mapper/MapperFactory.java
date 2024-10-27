package com.grocery.app.mapper;

import com.grocery.app.dto.FcmDTO;
import com.grocery.app.dto.MailDetailsDTO;
import com.grocery.app.dto.NotiDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MapperFactory {

    @Autowired
    private ModelMapper modelMapper;

    public MapperFactory(ModelMapper modelMapper){
        NotiMapper.toFcmDTO(modelMapper);
        NotiMapper.toMailDTOList(modelMapper);

    }

}
