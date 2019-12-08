package com.shadowsdream.service;

import com.shadowsdream.dao.implementations.PersonDaoImpl;
import com.shadowsdream.dto.*;
import com.shadowsdream.exception.DeleteOperationException;
import com.shadowsdream.exception.InsertOperationException;
import com.shadowsdream.exception.PersonServiceException;
import com.shadowsdream.exception.SelectOperationException;
import com.shadowsdream.model.Person;
import com.shadowsdream.model.PhoneNumber;
import com.shadowsdream.model.enums.Gender;
import com.shadowsdream.model.enums.PhoneType;
import com.shadowsdream.service.implementations.PersonServiceImpl;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class PersonServiceImplTest {

    private static PersonDaoImpl mockedPersonDao = null;

    private static PersonServiceImpl sut = null;

    private PersonSaveDto personSaveDto = null;

    private Person p = null;
    private Person p1 = null;
    private Person p2 = null;


    @BeforeEach
    void setUp() {
        mockedPersonDao = mock(PersonDaoImpl.class);

        sut = spy(new PersonServiceImpl(mockedPersonDao));

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

        p = new Person(1L, "F1", "L2", Gender.MALE,
                LocalDate.of(1990, Month.JANUARY, 12),
                "C1", "f1l1@foo.com", null);

        p1 = new Person(2L, "F2", "L2", Gender.MALE,
                LocalDate.of(1991, Month.FEBRUARY, 13),
                "C2", "f2l2@foo.com", null);

        p2 = new Person(3L, "F3", "L3", Gender.MALE,
                LocalDate.of(1990, Month.MARCH, 14),
                "C3", "f3l3@foo.com", null);

    }


    @Test
    @DisplayName("[save] SUT must invoke personDao's save() method exactly one time")
    void saveTest() throws Exception {
        sut.save(personSaveDto);
        verify(mockedPersonDao, times(1)).save(Mockito.isA(Person.class));
    }


    @Test
    @DisplayName("[save] Must throw PersonServiceException if it caches InsertOperationException")
    void savePersonServiceExOnInsertOperationExTest() {
        Throwable actual = assertThrows(PersonServiceException.class,
                () -> {
                    doThrow(InsertOperationException.class).when(mockedPersonDao).save(Mockito.isA(Person.class));
                    sut.save(personSaveDto);
                });

        assertTrue(actual.getMessage().contains("contact saving failed: "));
    }


    @Test
    @DisplayName("[findAll] SUT must return list of PersonViewDto")
    void findAllTest() throws Exception {

        PersonViewDto pWd = new PersonViewDto(1L, "F1", "L1");
        PersonViewDto pWd1 = new PersonViewDto(2L, "F2", "L2");
        PersonViewDto pWd2 = new PersonViewDto(3L, "F3", "L3");

        List<Person> fromDb = new ArrayList<>(3);
        fromDb.add(p);
        fromDb.add(p1);
        fromDb.add(p2);

        when(mockedPersonDao.findAll()).thenReturn(fromDb);

        List<PersonViewDto> actual = sut.findAll();

        verify(mockedPersonDao, times(1)).findAll();

        MatcherAssert.assertThat(actual, hasSize(3));
        MatcherAssert.assertThat(actual, hasItems(pWd, pWd1, pWd2));
    }


    @Test
    @DisplayName("[findById] SUT must return PersonDto")
    void findByIdTest() throws Exception {
        PersonDto expected = new PersonDto(1L, "F1", "L2", Gender.MALE,
                LocalDate.of(1990, Month.JANUARY, 12),
                "C1", "f1l1@foo.com", null);

        when(mockedPersonDao.findById(isA(Long.class))).thenReturn(p);

        PersonDto actual = sut.findById(1L);

        verify(mockedPersonDao, times(1)).findById(Mockito.isA(Long.class));

        assertEquals(expected, actual);
    }


    @Test
    @DisplayName("[findById] Must throw PersonServiceException if it caches SelectOperationException")
    void findByIdPersonServiceExOnSelectOperationExTest() {
        Throwable actual = assertThrows(PersonServiceException.class,
                () -> {
                    doThrow(SelectOperationException.class).when(mockedPersonDao).findById(Mockito.isA(Long.class));
                    sut.findById(Mockito.isA(Long.class));
                });

        assertTrue(actual.getMessage().contains("could not get details: "));
    }


    @Test
    @DisplayName("[updatePerson] SUT must invoke updatePerson() method exactly one time")
    void updatePersonTest() throws Exception {
        sut.updatePerson(new PersonDto());
        verify(mockedPersonDao, times(1)).updatePerson(Mockito.isA(Person.class));
    }


    @Test
    @DisplayName("[updatePhoneNumber] SUT must invoke updatePerson() method exactly one time")
    void updatePhoneNumberTest() throws Exception {
        sut.updatePhoneNumber(new PhoneNumberDto());
        verify(mockedPersonDao, times(1)).updatePhoneNumber(Mockito.isA(PhoneNumber.class));
    }


    @Test
    @DisplayName("[removePerson] SUT must invoke removePerson() method exactly one time")
    void removePersonTest() throws Exception {
        sut.removePerson(1L);
        verify(mockedPersonDao, times(1)).removePerson(Mockito.isA(Long.class));
    }


    @Test
    @DisplayName("[removePerson] Must throw PersonServiceException if it caches DeleteOperationException")
    void removePersonPersonServiceExOnDeleteOperationExTest() {
        Throwable actual = assertThrows(PersonServiceException.class,
                () -> {
                    doThrow(DeleteOperationException.class).when(mockedPersonDao).removePerson(Mockito.isA(Long.class));
                    sut.removePerson(Mockito.isA(Long.class));
                });

        assertTrue(actual.getMessage().contains("could not remove contact: "));
    }



    @Test
    @DisplayName("[removePhoneNumber] SUT must invoke removePhoneNumber() method exactly one time")
    void removePhoneNumberTest() throws Exception {
        sut.removePhoneNumber(1L);
        verify(mockedPersonDao, times(1)).removePhoneNumber(Mockito.isA(Long.class));
    }


    @Test
    @DisplayName("[removePhoneNumber] Must throw PersonServiceException if it caches DeleteOperationException")
    void removePhoneNumberPersonServiceExOnDeleteOperationExTest() {
        Throwable actual = assertThrows(PersonServiceException.class,
                () -> {
                    doThrow(DeleteOperationException.class).when(mockedPersonDao).removePhoneNumber(Mockito.isA(Long.class));
                    sut.removePhoneNumber(Mockito.isA(Long.class));
                });

        assertTrue(actual.getMessage().contains("ould not remove: "));
    }
}