package at.ac.tuwien.sepm.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.persistence.DataGeneratorBean;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseServiceTest {

  @Autowired
  HorseService horseService;

  @Autowired
  DataGeneratorBean bean;

  @BeforeEach
  public void setup() throws SQLException {
    bean.generateData();
  }

  @AfterEach
  public void cleanup() throws SQLException {
    bean.cleanData();
  }

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses()
        .toList();
    assertThat(horses.size()).isGreaterThanOrEqualTo(10);
    assertThat(horses)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }

  @Test
  public void creatingHorseWithoutDateOfBirthThrowsValidationexception() {
    HorseDetailDto horse = new HorseDetailDto(null,
            "Juan",
            null,
            null,
            Sex.MALE,
            null,
            null,
            null);
    assertThrows(ValidationException.class,
            () -> horseService.create(horse));
  }

  @Test
  public void changingSexOfHorseWithChildrenThrowsConflictexception() {
    HorseDetailDto horse = new HorseDetailDto(-1L,
            "Wendy",
            "The famous one!",
            LocalDate.of(2012, 12, 12),
            Sex.MALE,
            null,
            null,
            null);
    assertThrows(ConflictException.class,
            () -> horseService.update(horse));
  }

  @Test
  public void searchReturnsRightHorse() {
    HorseSearchDto searchParams = new HorseSearchDto("Carlo",
            null,
            null,
            Sex.MALE,
            null,
            null);
    List<HorseListDto> horses = horseService.search(searchParams).toList();
    assertThat(horses)
            .isNotNull()
            .map(HorseListDto::id, HorseListDto::name, HorseListDto::description, HorseListDto::dateOfBirth,
                    HorseListDto::sex)
            .contains(tuple(-3L, "Carlo", "Description 2", LocalDate.of(2016, 4, 14), Sex.MALE));
  }

  @Test
  public void createWithRightParametersReturnsCreatedHorse() throws Exception {
    HorseDetailDto horse = new HorseDetailDto(null,
            "Juan",
            null,
            LocalDate.of(2014, 12, 12),
            Sex.MALE,
            null,
            null,
            null);
    var created = horseService.create(horse);
    assertThat(created)
            .isNotNull()
            .extracting(HorseDetailDto::name, HorseDetailDto::dateOfBirth, HorseDetailDto::sex)
            .contains(horse.name(), horse.dateOfBirth(), horse.sex());
  }


}
