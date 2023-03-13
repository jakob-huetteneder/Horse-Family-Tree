package at.ac.tuwien.sepm.assignment.individual.service.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  public void validateForUpdate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horse.sex() == null) {
      validationErrors.add("Horse sex is not given");
    }

    if (horse.name() == null) {
      validationErrors.add("Horse name is not given");
    } else {
      if (horse.name().isBlank()) {
        validationErrors.add("Horse name is given but blank");
      }
      if (horse.name().length() > 255) {
        validationErrors.add("Horse name too long: longer than 255 characters");
      }
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("Horse date of birth is not given");
    } else {
      if (horse.dateOfBirth().isAfter(LocalDate.now())) {
        validationErrors.add("Given date of birth is in the future");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }

  public void validateForCreate(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horse.sex() == null) {
      validationErrors.add("Horse sex is not given");
    }

    if (horse.name() == null) {
      validationErrors.add("Horse name is not given");
    } else {
      if (horse.name().isBlank()) {
        validationErrors.add("Horse name is given but blank");
      }
      if (horse.name().length() > 255) {
        validationErrors.add("Horse name too long: longer than 255 characters");
      }
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("Horse date of birth is not given");
    } else {
      if (horse.dateOfBirth().isAfter(LocalDate.now())) {
        validationErrors.add("Given date of birth is in the future");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }
  }



}
