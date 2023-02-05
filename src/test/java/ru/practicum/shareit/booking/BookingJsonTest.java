package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.dto.OutputBookingDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.util.BookingTestUtil.BOOKING_ID;
import static ru.practicum.shareit.util.BookingTestUtil.getOutputBookingDto;
import static ru.practicum.shareit.util.ItemTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;

@JsonTest
public class BookingJsonTest {
    @Autowired
    private JacksonTester<InputBookingDto> jsonInput;
    @Autowired
    private JacksonTester<OutputBookingDto> jsonOutput;
    private final LocalDateTime dt = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final InputBookingDto inputBookingDto = new InputBookingDto(BOOKING_ID, dt, dt.plusDays(1), ITEM_ID);
    private final OutputBookingDto outputBookingDto = getOutputBookingDto(dt);

    @Test
    void bookingInputDtoTest() throws IOException {
        JsonContent<InputBookingDto> jsonDto = jsonInput.write(inputBookingDto);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(BOOKING_ID.intValue());
        assertThat(jsonDto).extractingJsonPathStringValue("$.start").isEqualTo(dt.format(FORMATTER));
        assertThat(jsonDto).extractingJsonPathStringValue("$.end").isEqualTo(dt.plusDays(1).format(FORMATTER));
        assertThat(jsonDto).extractingJsonPathNumberValue("$.itemId").isEqualTo(ITEM_ID.intValue());

        InputBookingDto parsed = jsonInput.parse(jsonDto.getJson()).getObject();
        assertEquals(inputBookingDto, parsed);
    }

    @Test
    void bookingOutputDtoTest() throws IOException {
        outputBookingDto.setBooker(UserMapper.mapToUserDto(getUser()));

        JsonContent<OutputBookingDto> jsonDto = jsonOutput.write(outputBookingDto);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(BOOKING_ID.intValue());
        assertThat(jsonDto).extractingJsonPathStringValue("$.start").isEqualTo(dt.format(FORMATTER));
        assertThat(jsonDto).extractingJsonPathStringValue("$.end").isEqualTo(dt.plusDays(1).format(FORMATTER));
        assertThat(jsonDto).extractingJsonPathNumberValue("$.item.id").isEqualTo(ITEM_ID.intValue());
        assertThat(jsonDto).extractingJsonPathNumberValue("$.booker.id").isEqualTo(USER_ID.intValue());

        OutputBookingDto parsed = jsonOutput.parse(jsonDto.getJson()).getObject();
        assertEquals(outputBookingDto, parsed);
    }
}
