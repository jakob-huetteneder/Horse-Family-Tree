package at.ac.tuwien.sepm.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseDaoTest {

  @Autowired
  HorseDao horseDao;
  @Autowired
  DataGeneratorBean bean;

  @BeforeEach
  public void setup() throws SQLException {
    bean.generateData();
  }

  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(7); // TODO adapt to exact number of elements in test data later
    assertThat(horses)
        .extracting(Horse::getId, Horse::getName)
        .contains(tuple(-1L, "Wendy"));
  }

  @Test
  public void getByIdWithNonExistingIdThrowsNotFoundException() {
    assertThrows(NotFoundException.class,
            () -> horseDao.getById(-10L));
  }

  @Test
  public void createWithExistingIdThrowsConflictException() {
    HorseDetailDto horse = new HorseDetailDto(-1L,
            "Juan",
            null,
            LocalDate.of(2014, 12, 12),
            Sex.MALE,
            null,
            null,
            null);
    assertThrows(ConflictException.class,
            () -> horseDao.create(horse));
  }

  @Test
  public void searchReturnsRightHorse() {
    HorseSearchDto searchParams = new HorseSearchDto("sy",
            null,
            LocalDate.of(2017, 3, 13),
            null,
            null,
            null);
    List<Horse> horses = horseDao.search(searchParams).stream().toList();
    assertThat(horses)
            .isNotNull()
            .map(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex)
            .contains(tuple(-2L, "Issy", "Description 1", LocalDate.of(2014, 3, 13), Sex.FEMALE));
  }

  @Test
  public void deleteRemovesHorseWithGivenId() throws Exception {
    horseDao.delete(-1L);
    List<Horse> horses = horseDao.getAll();
    assertThat(horses)
            .map(Horse::getId, Horse::getName, Horse::getDescription, Horse::getDateOfBirth, Horse::getSex)
          .doesNotContain(tuple(-1L, "Wendy", "The famous one!", LocalDate.of(2012, 12, 12), Sex.FEMALE));

  }



}
