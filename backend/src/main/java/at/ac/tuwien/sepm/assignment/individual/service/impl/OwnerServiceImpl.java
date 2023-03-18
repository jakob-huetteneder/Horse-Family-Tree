package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.OwnerMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.OwnerDao;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OwnerServiceImpl implements OwnerService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final OwnerDao dao;
  private final OwnerMapper mapper;

  public OwnerServiceImpl(
      OwnerDao dao,
      OwnerMapper mapper) {
    this.dao = dao;
    this.mapper = mapper;
  }

  @Override
  public OwnerDto getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    return mapper.entityToDto(dao.getById(id));
  }

  @Override
  public Map<Long, OwnerDto> getAllById(Collection<Long> ids) throws NotFoundException {
    LOG.trace("getAllById({})", ids);
    Map<Long, OwnerDto> owners =
        dao.getAllById(ids).stream()
            .map(mapper::entityToDto)
            .collect(Collectors.toUnmodifiableMap(OwnerDto::id, Function.identity()));
    for (final var id : ids) {
      if (!owners.containsKey(id)) {
        throw new NotFoundException("Owner with ID %d not found".formatted(id));
      }
    }
    return owners;
  }

  @Override
  public Stream<OwnerDto> search(OwnerSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    return dao.search(searchParameters).stream()
        .map(mapper::entityToDto);
  }

  @Override
  public OwnerDto create(OwnerCreateDto newOwner) throws ValidationException {
    LOG.trace("create({})", newOwner);
    List<String> validationErrors = new ArrayList<>();

    if (newOwner.firstName() == null) {
      validationErrors.add("Owner first name is not given");
    } else {
      if (newOwner.firstName().isBlank()) {
        validationErrors.add("Owner first name is given but blank");
      }
      if (newOwner.firstName().length() > 255) {
        validationErrors.add("Owner first name too long: longer than 255 characters");
      }
    }

    if (newOwner.lastName() == null) {
      validationErrors.add("Owner last name is not given");
    } else {
      if (newOwner.lastName().isBlank()) {
        validationErrors.add("Owner last name is given but blank");
      }
      if (newOwner.lastName().length() > 255) {
        validationErrors.add("Owner last name too long: longer than 255 characters");
      }
    }

    if(newOwner.email() != null){
      if (newOwner.email().isBlank()) {
        validationErrors.add("Owner email is given but blank");
      }
      if (newOwner.email().length() > 255) {
        validationErrors.add("Owner email too long: longer than 255 characters");
      }
    }
    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of owner for create failed", validationErrors);
    }

    return mapper.entityToDto(dao.create(newOwner));
  }
}
