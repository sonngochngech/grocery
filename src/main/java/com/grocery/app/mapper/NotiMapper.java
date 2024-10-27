package com.grocery.app.mapper;

import com.grocery.app.dto.FcmDTO;
import com.grocery.app.dto.MailDetailsDTO;
import com.grocery.app.dto.NotiDTO;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;

import java.util.ArrayList;
import java.util.List;

public class NotiMapper {
    public static  void toMailDTOList(ModelMapper modelMapper){
        Converter<NotiDTO, List<MailDetailsDTO>> toMailDetailsList = new Converter<NotiDTO, List<MailDetailsDTO>>() {
            @Override
            public List<MailDetailsDTO> convert(MappingContext<NotiDTO, List<MailDetailsDTO>> context) {
                NotiDTO source = context.getSource();
                List<MailDetailsDTO> mailDetailsList = new ArrayList<>();
                source.getMeanDTO().getEmails().forEach(email->{
                    MailDetailsDTO mailDetailsDTO = new MailDetailsDTO();
                    mailDetailsDTO.setSubject(source.getNotiContentDTO().getTitle());
                    mailDetailsDTO.setMessage(source.getNotiContentDTO().getMessage());
                    mailDetailsDTO.setRecipient(email);
                    mailDetailsList.add(mailDetailsDTO);
                });
                return mailDetailsList;
            }
        };
        modelMapper.addConverter(toMailDetailsList);
    }

    public static void toFcmDTO(ModelMapper modelMapper){
        Converter<NotiDTO, FcmDTO> toFcmDTO = new Converter<NotiDTO, FcmDTO>() {
            @Override
            public FcmDTO convert(MappingContext<NotiDTO, FcmDTO> context) {
                NotiDTO source = context.getSource();
                FcmDTO fcmDTO = new FcmDTO();
                fcmDTO.setNotiContentDTO(source.getNotiContentDTO());
                fcmDTO.setDevices(source.getMeanDTO().getDevices());
                return fcmDTO;
            }
        };
        modelMapper.addConverter(toFcmDTO);
    }
}
