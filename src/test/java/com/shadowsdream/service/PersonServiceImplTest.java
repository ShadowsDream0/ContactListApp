package com.shadowsdream.service;

import com.shadowsdream.dao.PersonDaoImpl;
import com.shadowsdream.dto.PersonSaveDto;
import com.shadowsdream.dto.PhoneNumberSaveDto;
import com.shadowsdream.model.Person;
import com.shadowsdream.model.enums.Gender;
import com.shadowsdream.model.enums.PhoneType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.whenNew;

class PersonServiceImplTest {

    private static PersonDaoImpl mockedPersonDao = null;
    private static DataSource mockedDataSource = null;

    @InjectMocks
    private static PersonServiceImpl spiedPersonService = null;

    private PersonSaveDto personSaveDto = null;

    @BeforeAll
    static void setUpAll() throws Exception {
        mockedPersonDao = mock(PersonDaoImpl.class);
        mockedDataSource = mock(DataSource.class);
        spiedPersonService = spy(PersonServiceImpl.class);

        when(spiedPersonService.save(new PersonSaveDto()));

        //whenNew(PersonDaoImpl.class).withAnyArguments().thenReturn(mockedPersonDao);

        //when(mockedPersonDao.save(new Person())).thenReturn(1L);

    }

    @BeforeEach
    void setUp() {
        personSaveDto = PersonSaveDto.builder()
                .firstName("Blah")
                .lastName("Blaher")
                .gender(Gender.MALE)
                .birthday(LocalDate.now())
                .city("Blah-city")
                .email("blah@blah.com")
                .phoneNumbers(Arrays.asList(
                                            PhoneNumberSaveDto.builder()
                                                            .phone("123")
                                                            .type(PhoneType.WORK)
                                                            .build()
                                            )
                )
                .build();
    }

    @Test
    void save() throws Exception {
        //verifyNew(PersonDaoImpl.class).withArguments(mockedPersonDao);
        Long actual = spiedPersonService.save(personSaveDto);
        assertEquals(1L, actual);
    }

    @Disabled
    @Test
    void findAll() {
    }

    @Disabled
    @Test
    void findById() {
    }

    @Disabled
    @Test
    void updatePerson() {
    }

    @Disabled
    @Test
    void updatePhoneNumber() {
    }

    @Disabled
    @Test
    void removePerson() {
    }

    @Disabled
    @Test
    void removePhoneNumber() {
    }
}