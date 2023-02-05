package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.InputItemDto;
import ru.practicum.shareit.item.dto.OutputItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.util.BookingTestUtil.*;
import static ru.practicum.shareit.util.CommentTestUtil.getOutputCommentDto;
import static ru.practicum.shareit.util.ItemTestUtil.*;
import static ru.practicum.shareit.util.UserTestUtil.*;

@JsonTest
public class ItemJsonTest {
    @Autowired
    private JacksonTester<InputItemDto> jsonInput;
    @Autowired
    private JacksonTester<OutputItemDto> jsonOutput;
    private final InputItemDto inputItemDto = getInputItemDto();
    private final OutputItemDto outputItemDto = getOutputDto(USER_ID);
    private final LocalDateTime dt = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);

    @Test
    void itemInputDtoTest() throws IOException {
        inputItemDto.setId(ITEM_ID);

        JsonContent<InputItemDto> jsonDto = jsonInput.write(inputItemDto);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(ITEM_ID.intValue());
        assertThat(jsonDto).extractingJsonPathStringValue("$.name").isEqualTo(ITEM_NAME);
        assertThat(jsonDto).extractingJsonPathStringValue("$.description").isEqualTo(ITEM_DESCR);
        assertThat(jsonDto).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(jsonDto).hasEmptyJsonPathValue("$.requestId");

        InputItemDto parsed = jsonInput.parse(jsonDto.getJson()).getObject();
        assertEquals(inputItemDto, parsed);
    }

    @Test
    void itemOutputDtoTest() throws IOException {
        outputItemDto.setId(ITEM_ID);
        outputItemDto.setComments(List.of(getOutputCommentDto()));
        outputItemDto.setLastBooking(BookingMapper.mapToShortBookingDto(getBooking(dt.minusDays(3))));
        outputItemDto.setNextBooking(BookingMapper.mapToShortBookingDto(getBooking(dt.plusDays(3))));

        JsonContent<OutputItemDto> jsonDto = jsonOutput.write(outputItemDto);
        assertThat(jsonDto).extractingJsonPathNumberValue("$.id").isEqualTo(ITEM_ID.intValue());
        assertThat(jsonDto).extractingJsonPathStringValue("$.name").isEqualTo(ITEM_NAME);
        assertThat(jsonDto).extractingJsonPathStringValue("$.description").isEqualTo(ITEM_DESCR);
        assertThat(jsonDto).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(jsonDto).hasEmptyJsonPathValue("$.requestId");
        assertThat(jsonDto).extractingJsonPathNumberValue("$.lastBooking.id")
                .isEqualTo(BOOKING_ID.intValue());
        assertThat(jsonDto).extractingJsonPathNumberValue("$.nextBooking.id")
                .isEqualTo(BOOKING_ID.intValue());
        assertThat(jsonDto).extractingJsonPathNumberValue("$.comments.length()").isEqualTo(1);

        InputItemDto parsed = jsonInput.parse(jsonDto.getJson()).getObject();
        assertEquals(inputItemDto, parsed);
    }
}
