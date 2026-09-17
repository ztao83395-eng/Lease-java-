package com.atxiaomian.lease.web.admin.custom.converter;

import com.atxiaomian.lease.model.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@Component
public class StringToBaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    @Override
    public <T extends BaseEnum> Converter<String,T> getConverter(Class<T> targetype){
        return new Converter<String, T>() {
            @Override
            public T convert(String code) {
                T[] eumConstants=targetype.getEnumConstants();
                for (T eumConstant:eumConstants){
                    if (eumConstant.getCode().equals(Integer.valueOf(code))){
                        return eumConstant;
                    }
                }
                throw new IllegalArgumentException("code:"+code+"非法");
            }
        };
    }
}
