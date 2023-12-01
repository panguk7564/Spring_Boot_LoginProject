package MineAPI_KaKao.Util;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StringArrayConverter implements AttributeConverter<List<String>, String> { // -- 문자열 리스트로 변환
    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {

        return attribute.stream().map(String::valueOf).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {

        if (dbData == null){
            return Collections.emptyList();
        }
        else {

            return Arrays.stream(dbData.split(SPLIT_CHAR))
                    .map(String::valueOf)
                    .collect(Collectors.toList());
        }
    }
}