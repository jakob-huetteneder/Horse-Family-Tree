package at.ac.tuwien.sepm.assignment.individual.rest;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable long id) throws NotFoundException {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    return service.getById(id);
  }


  @PutMapping("{id}")
  public HorseDetailDto update(@PathVariable long id, @RequestBody HorseDetailDto toUpdate) throws ValidationException, ConflictException, NotFoundException {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    return service.update(toUpdate.withId(id));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public HorseDetailDto create(@RequestBody HorseDetailDto toCreate) throws ValidationException, ConflictException, NotFoundException {
    LOG.info("POST " + BASE_PATH + "/{}", toCreate);
    LOG.debug("Body of request:\n{}", toCreate);
    return service.create(toCreate);

  }

  @DeleteMapping("{id}")
  public HorseDetailDto delete(@PathVariable long id) throws NotFoundException {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    return service.delete(id);

  }
}
