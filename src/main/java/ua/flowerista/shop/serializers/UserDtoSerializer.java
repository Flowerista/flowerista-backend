package ua.flowerista.shop.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import ua.flowerista.shop.dto.user.UserDto;

import java.io.IOException;

public class UserDtoSerializer extends JsonSerializer<UserDto> {

    @Override
    public void serialize(UserDto userDto, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", userDto.getId());
        jsonGenerator.writeStringField("firstName", userDto.getFirstName());
        jsonGenerator.writeStringField("lastName", userDto.getLastName());
        jsonGenerator.writeStringField("email", userDto.getEmail());
        jsonGenerator.writeStringField("phoneNumber", "0" + userDto.getPhoneNumber());
        jsonGenerator.writeStringField("role", userDto.getRole());
        jsonGenerator.writeEndObject();
    }
}
