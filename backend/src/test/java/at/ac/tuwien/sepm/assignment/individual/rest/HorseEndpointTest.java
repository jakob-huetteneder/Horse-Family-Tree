package at.ac.tuwien.sepm.assignment.individual.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.persistence.DataGeneratorBean;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  DataGeneratorBean bean;

  @BeforeEach
  public void setup() throws SQLException {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    bean.generateData();
  }

  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = objectMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult.size()).isGreaterThanOrEqualTo(7); // TODO adapt this to the exact number in the test data later
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name)
        .contains(tuple(-1L, "Wendy"));
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }
  @Test
  public void postingHorseWithoutDateOfBirthReturns422() throws Exception {
    HorseDetailDto horse = new HorseDetailDto(null,
            "Juan",
            null,
            null,
            Sex.MALE,
            null,
            null,
            null);
    mockMvc
            .perform(MockMvcRequestBuilders
                    .post("/horses")
                    .content(objectMapper.writeValueAsBytes(horse))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity());
  }


  @Test
  public void postingHorseWithRightParamsReturns201() throws Exception {
    HorseDetailDto horse = new HorseDetailDto(null,
            "Juan",
            null,
            LocalDate.now(),
            Sex.MALE,
            null,
            null,
            null);
    mockMvc
            .perform(MockMvcRequestBuilders
                    .post("/horses")
                    .content(objectMapper.writeValueAsBytes(horse))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated());
  }

  @Test
  public void gettingExistingUrlReturns200() throws Exception {
    mockMvc
            .perform(MockMvcRequestBuilders
                    .get("/horses/-1")
            ).andExpect(status().isOk());
  }

  @Test
  public void changingSexOfHorseWithChildrenReturns409() throws Exception {
    HorseDetailDto horse = new HorseDetailDto(-1L,
            "Wendy",
            "The famous one!",
            LocalDate.of(2012, 12, 12),
            Sex.MALE,
            null,
            null,
            null);
    mockMvc
            .perform(MockMvcRequestBuilders
                    .put("/horses/-1")
                    .content(objectMapper.writeValueAsBytes(horse))
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict());
  }



}
