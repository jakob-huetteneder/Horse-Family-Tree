package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepm.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.service.HorseService;
import at.ac.tuwien.sepm.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    var ownerIds = horses.stream()
        .map(Horse::getOwnerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> {
          try {
            return mapper.entityToListDto(horse, ownerMap, getMother(horse), getFather(horse));
          } catch (NotFoundException e) {
            throw new FatalException("Horse, that is already persisted, refers to non-existing parent", e);
          }
        });
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);
    validator.validateForUpdate(horse);
    validator.checkMotherFather(horse);
    List<Horse> children = dao.getChildren(horse.id());
    List<String> conflictErrors = new ArrayList<>();


    HorseDetailDto oldHorse = getById(horse.id());
    if (oldHorse.sex() != horse.sex()) {
      if (!children.isEmpty()) {
        conflictErrors.add("Cant change sex if the horse has children");
      }
    }
    for (Horse c :
            children) {
      if (horse.dateOfBirth().isAfter(c.getDateOfBirth())) {
        conflictErrors.add("Horse cannot be born after one or more children");
      }
    }
    conflictErrors.addAll(checkIfMotherFatherOwnerExists(horse));

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for update failed", conflictErrors);
    }

    var updatedHorse = dao.update(horse);
    return mapper.entityToDetailDto(
        updatedHorse,
        ownerMapForSingleId(updatedHorse.getOwnerId()),
        getMother(updatedHorse),
        getFather(updatedHorse)
    );
  }

  @Override
  public HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException, NotFoundException {
    LOG.trace("create({})", horse);
    validator.validateForCreate(horse);
    validator.checkMotherFather(horse);
    List<String> conflictErrors = checkIfMotherFatherOwnerExists(horse);

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Validation of horse for update failed", conflictErrors);
    }

    var createdHorse = dao.create(horse);
    return mapper.entityToDetailDto(
            createdHorse,
            ownerMapForSingleId(createdHorse.getOwnerId()),
            getMother(createdHorse),
            getFather(createdHorse));
  }



  @Override
  public HorseDetailDto delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    Horse horse = dao.delete(id);
    return mapper.entityToDetailDto(
            horse,
            ownerMapForSingleId(horse.getOwnerId()),
            getMother(horse),
            getFather(horse));
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.getOwnerId()),
        getMother(horse),
        getFather(horse));
  }


  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    LOG.trace("search()");
    var horses = dao.search(searchParameters);
    var ownerIds = horses.stream()
            .map(Horse::getOwnerId)
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
            .map(horse -> {
              try {
                return mapper.entityToListDto(horse, ownerMap, getMother(horse), getFather(horse));
              } catch (NotFoundException e) {
                throw new FatalException("Horse, that is already persisted, refers to non-existing parent", e);
              }
            });
  }




  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

  private HorseDetailDto getMother(Horse horse) throws NotFoundException {
    return horse.getMotherId() == null ? null : getById(horse.getMotherId());
  }

  private HorseDetailDto getFather(Horse horse) throws NotFoundException {
    return horse.getFatherId() == null ? null : getById(horse.getFatherId());
  }

  private List<String> checkIfMotherFatherOwnerExists(HorseDetailDto horse) {
    List<String> errors = new ArrayList<>();
    if (horse.ownerId() != null) {
      try {
        ownerService.getById(horse.ownerId());
      } catch (NotFoundException e) {
        errors.add("Given owner does not exist");
      }
    }
    if (horse.motherId() != null) {
      try {
        getById(horse.motherId());
      } catch (NotFoundException e) {
        errors.add("Given mother does not exist");
      }
    }
    if (horse.fatherId() != null) {
      try {
        getById(horse.fatherId());
      } catch (NotFoundException e) {
        errors.add("Given father does not exist");
      }
    }
    return errors;
  }


}
