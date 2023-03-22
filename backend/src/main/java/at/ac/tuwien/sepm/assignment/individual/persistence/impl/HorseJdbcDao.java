package at.ac.tuwien.sepm.assignment.individual.persistence.impl;

import at.ac.tuwien.sepm.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepm.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepm.assignment.individual.entity.Horse;
import at.ac.tuwien.sepm.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepm.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepm.assignment.individual.type.Sex;

import java.lang.invoke.MethodHandles;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;




@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";

  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , description = ?"
      + "  , date_of_birth = ?"
      + "  , sex = ?"
      + "  , owner_id = ?"
      + "  , mother_id = ?"
      + "  , father_id = ?"
      + " WHERE id = ?";

  private static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME
          + "(name, description, date_of_birth, sex, owner_id, mother_id, father_id) "
          + "VALUES (?, ?, ?, ?, ?, ?, ?)";

  private final JdbcTemplate jdbcTemplate;

  public HorseJdbcDao(
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcTemplate.query(SQL_SELECT_ALL, this::mapRow);
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.get(0);
  }


  @Override
  public Collection<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    String query = SQL_SELECT_ALL;
    ArrayList<String> params = new ArrayList<>();
    boolean first = true;

    if (searchParameters.ownerName() != null && !searchParameters.ownerName().isBlank()) {
      query += " JOIN owner ON horse.owner_id = owner.id " +
              "WHERE UPPER(owner.first_name||' '||owner.last_name) like UPPER('%'||COALESCE(?, '')||'%')";
      params.add(searchParameters.ownerName());
      first = false;
    }

    if (searchParameters.name() != null && !searchParameters.name().isBlank()) {
      if (first) {
        query += " WHERE UPPER(name) like UPPER('%'||COALESCE(?, '')||'%')";
      } else {
        query += " AND UPPER(name) like UPPER('%'||COALESCE(?, '')||'%')";
      }
      params.add(searchParameters.name());
      first = false;
    }

    if (searchParameters.description() != null && !searchParameters.description().isBlank()) {
      if (first) {
        query += " WHERE UPPER(description) like UPPER('%'||COALESCE(?, '')||'%')";
      } else {
        query += " AND UPPER(description) like UPPER('%'||COALESCE(?, '')||'%')";
      }
      params.add(searchParameters.description());
      first = false;
    }

    if (searchParameters.bornBefore() != null){
      if (first){
        query += " WHERE date_of_birth < ?";
      } else {
        query += " AND date_of_birth < ?";
      }
      params.add(searchParameters.bornBefore().toString());
      first = false;
    }

    if (searchParameters.sex() != null){
      if (first){
        query += " WHERE sex = ?";
      } else {
        query += " AND sex = ?";
      }
      params.add(searchParameters.sex().toString());
    }

    if (searchParameters.limit() != null){

      query += " LIMIT ?";
      params.add(searchParameters.limit().toString());
    }

    return jdbcTemplate.query(query, this::mapRow, params.toArray());
  }




  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex().toString(),
        horse.ownerId(),
        horse.motherId(),
        horse.fatherId(),
        horse.id()
    );
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setDescription(horse.description())
        .setDateOfBirth(horse.dateOfBirth())
        .setSex(horse.sex())
        .setOwnerId(horse.ownerId())
        .setMotherId(horse.motherId())
        .setFatherId(horse.fatherId())
        ;
  }

  @Override
  public Horse create(HorseDetailDto horse) {
    LOG.trace("create({})", horse);

    GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement stmt = con.prepareStatement(SQL_CREATE,
              Statement.RETURN_GENERATED_KEYS);
      stmt.setString(1, horse.name());
      stmt.setString(2, horse.description());
      stmt.setString(3, horse.dateOfBirth().toString());
      stmt.setString(4, horse.sex().toString());
      stmt.setString(5, horse.ownerId() != null ? horse.ownerId().toString() : null);
      stmt.setString(6, horse.motherId() != null ? horse.motherId().toString() : null);
      stmt.setString(7, horse.fatherId() != null ? horse.fatherId().toString() : null);
      return stmt;
    }, keyHolder);

    Number key = keyHolder.getKey();
    if (key == null) {
      // This should never happen. If it does, something is wrong with the
      //DB or the way the prepared statement is set up.
      throw new FatalException("Could not extract key for newly created horse. There is probably a programming error…");
    }

    return new Horse()
            .setId(key.longValue())
            .setName(horse.name())
            .setDescription(horse.description())
            .setDateOfBirth(horse.dateOfBirth())
            .setSex(horse.sex())
            .setOwnerId(horse.ownerId())
            .setMotherId(horse.motherId())
            .setFatherId(horse.fatherId())
            ;
  }

  @Override
  public Horse delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);

    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    int deletedHorse = jdbcTemplate.update(SQL_DELETE_BY_ID, id);

    if (deletedHorse == 0) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }

    return horses.get(0);
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setDescription(result.getString("description"))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setSex(Sex.valueOf(result.getString("sex")))
        .setOwnerId(result.getObject("owner_id", Long.class))
        .setMotherId(result.getObject("mother_id", Long.class))
        .setFatherId(result.getObject("father_id", Long.class))
        ;
  }
}
