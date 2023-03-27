package at.ac.tuwien.sepm.assignment.individual.persistence;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;

import java.util.Collection;
import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();


  /**
   * Update the horse with the ID given in {@code horse}
   *  with the data given in {@code horse}
   *  in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseDetailDto horse) throws NotFoundException;

  /**
   * Creates a new horse with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to create
   * @return the created horse
   * @throws ConflictException if the data given for the horse is in conflict with the data currently in the system (owner does not exist, …)
   */
  Horse create(HorseDetailDto horse) throws ConflictException;

  /**
   * Delete the horse with given ID from the
   * persistent data store.
   *
   * @param id the ID of the horse to delete.
   * @return the deleted horse with ID {@code id}
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   */
  Horse delete(long id) throws NotFoundException;

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Search horses that match the given parameters in the
   * persistent data store.
   *
   * @param searchParameters parameters to search horses by
   * @return Collection of horses which match with the parameters
   */
  Collection<Horse> search(HorseSearchDto searchParameters);

  /**
   * Get all children of the horse with ID {@code id}
   * from the persistent data store.
   *
   * @param id id of the horse to get the children of
   * @return list of children of the horse with ID {@code id}
   */
  List<Horse> getChildren(Long id);

}
