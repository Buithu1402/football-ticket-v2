package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.model.PagingContainer;
import com.app.footballticketservice.model.User;
import com.app.footballticketservice.utils.PagingUtil;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final NamedParameterJdbcTemplate writeDB;

    public UserService(@WriteDB NamedParameterJdbcTemplate writeDB) {
        this.writeDB = writeDB;
    }

    @Transactional
    public void manage(long uid, String action) {
        var sql = """
                UPDATE users
                SET status = :status
                WHERE user_id = :id
                """;
        var param = new MapSqlParameterSource()
                .addValue("id", uid)
                .addValue("status", action);
        writeDB.update(sql, param);
    }

    public PagingContainer<User> getAll(int page, int size) {
        var sql = """
                SELECT * 
                FROM users
                WHERE status <> 'INACTIVE' and role = 'USER'
                LIMIT :limit OFFSET :offset
                """;
        var offset = PagingUtil.calculateOffset(page, size);
        var param = new MapSqlParameterSource()
                .addValue("limit", size)
                .addValue("offset", offset);
        var users = writeDB.query(sql, param, (rs, rowNum) -> new User(rs));
        var total = writeDB.queryForObject(
                "SELECT COUNT(1) FROM users WHERE status <> 'INACTIVE' and role = 'USER'",
                new MapSqlParameterSource(),
                Integer.class
        );
        return new PagingContainer<>(page, size, total, users);
    }
}
