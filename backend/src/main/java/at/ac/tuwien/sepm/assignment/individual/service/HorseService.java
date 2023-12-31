package at.ac.tuwien.sepm.assignment.individual.service;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.exception.ValidationException;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Lists all horses stored in the system.
   *
   * @return list of all stored horses
   */
  Stream<HorseListDto> allHorses();


  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException;

  /**
   * Creates a new horse with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to create
   * @return the created horse
   * @throws ValidationException if the data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the data given for the horse is in conflict with the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException, NotFoundException;

  /**
   * Delete the horse with given ID from the
   * persistent data store.
   *
   * @param id the ID of the horse to delete.
   * @return the deleted horse with ID {@code id}
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   */
  HorseDetailDto delete(long id) throws NotFoundException;

  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;

  /**
   * Search horses that match the given parameters in the
   * persistent data store.
   *
   * @param searchParameters parameters to search horses by
   * @return stream of horses which match with the parameters
   */
  Stream<HorseListDto> search(HorseSearchDto searchParameters);
}
