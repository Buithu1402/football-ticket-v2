package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.model.PagingContainer;
import com.app.footballticketservice.model.Seat;
import com.app.footballticketservice.model.Stadium;
import com.app.footballticketservice.model.Type;
import com.app.footballticketservice.request.payload.StadiumPayload;
import com.app.footballticketservice.utils.PagingUtil;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class StadiumService {
    private final NamedParameterJdbcTemplate writeDb;

    public StadiumService(@WriteDB NamedParameterJdbcTemplate writeDb) {
        this.writeDb = writeDb;
    }


    public List<Type> findSeatType() {
        var sql = """
                      select *
                        from type
                """;
        return writeDb.query(sql, new MapSqlParameterSource(), BeanPropertyRowMapper.newInstance(Type.class));
    }

    @Transactional
    public void delete(long stadiumId) {
        var sql = """
                      update stadium
                      set status = 'inactive'
                      where stadium_id = :stadiumId;
                """;
        var params = new MapSqlParameterSource()
                .addValue("stadiumId", stadiumId);
        writeDb.update(sql, params);
    }

    @Transactional
    public void deleteSeat(long seatId) {
        var sql = """
                      update seat
                      set status = 'inactive'
                      where seat_id = :seatId ;
                """;
        var params = new MapSqlParameterSource()
                .addValue("seatId", seatId);
        writeDb.update(sql, params);
    }

    public List<Seat> findSeats(long stadiumId) {
        var sql = """
                      select s.seat_id,
                             s.seat_number,
                             s.stadium_id,
                             t.type_id,
                             t.name as typeName
                        from seat s
                               inner join type t on s.type_id = t.type_id
                        where s.stadium_id = :stadiumId
                        and s.status = 'active'
                """;
        var params = new MapSqlParameterSource()
                .addValue("stadiumId", stadiumId);
        return writeDb.query(sql, params, BeanPropertyRowMapper.newInstance(Seat.class));
    }

    public PagingContainer<Stadium> findAll(int page, int size, String key) {
        var offset = PagingUtil.calculateOffset(page, size);
        var sql = """
                              select *
                              from stadium
                              where true
                                and status = 'active'
                                and (:stadium = '%%' OR :stadium IS NULL OR stadium_name like :stadium)
                              limit :size offset :offset;
                """;
        var params = new MapSqlParameterSource()
                .addValue("stadium", "%" + key + "%")
                .addValue("size", size)
                .addValue("offset", offset);
        var result = writeDb.query(sql, params, BeanPropertyRowMapper.newInstance(Stadium.class));
        var sqlTotal = """
                              select count(1)
                              from stadium
                              where true
                                and status = 'active'
                                and stadium_name like '%:stadium%';
                """;
        var total = writeDb.queryForObject(sqlTotal, params, Integer.class);
        return new PagingContainer<>(page, size, total, result);
    }

    @Transactional
    public Long save(String name, String image, String address) {
        var keyHolder = new GeneratedKeyHolder();
        var sql = """
                    insert into stadium (stadium_name, image, address)
                    values (:stadiumName, :image, :address);
                """;
        var params = new MapSqlParameterSource()
                .addValue("stadiumName", name)
                .addValue("image", image)
                .addValue("address", address);
        writeDb.update(sql, params, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Transactional
    public void save(Long id, String name, String image, String address) {
        var sql = """
                        update stadium
                        set stadium_name = IFNULL(:stadiumName, stadium_name),
                            image = IFNULL(:image, image),
                            address = IFNULL(:address, address)
                        where stadium_id = :stadiumId;
                """;
        var params = new MapSqlParameterSource()
                .addValue("stadiumId", id)
                .addValue("stadiumName", name)
                .addValue("image", image)
                .addValue("address", address);
        writeDb.update(sql, params);
    }

    @Transactional
    public void save(StadiumPayload payload, String image) {
        final long stadiumId;
        var sql = "";
        if (payload.stadiumId() < 1) {
            stadiumId = save(payload.stadiumName(), image, payload.address());
            sql = """
                    insert into seat(stadium_id, seat_number, type_id)
                    values (:stadiumId, :seatNumber, :typeId)
                    """;
        } else {
            save(payload.stadiumId(), payload.stadiumName(), image, payload.address());
            stadiumId = payload.stadiumId();
            sql = "CALL up_UpsertSeat(:seatId, :seatNumber, :stadiumId, :typeId)";
        }
        var params = payload.seats()
                            .stream()
                            .map(seat -> IntStream
                                    .rangeClosed(seat.start(), seat.end())
                                    .mapToObj(number -> new MapSqlParameterSource()
                                            .addValue("stadiumId", stadiumId)
                                            .addValue(
                                                    "seatNumber",
                                                    seat.prefix() + number
                                            )
                                            .addValue("seatId", seat.seatId())
                                            .addValue("typeId", seat.typeId() == 0 ? 1 : seat.typeId())
                                    )
                                    .toList()

                            )
                            .flatMap(List::stream)
                            .toList();
        var s = params
                .toArray(new MapSqlParameterSource[0]);
        writeDb.batchUpdate(sql, s);
    }
}
