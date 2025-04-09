package com.app.footballticketservice.service;

import com.app.footballticketservice.config.db.WriteDB;
import com.app.footballticketservice.dto.BookSendEmailDTO;
import com.app.footballticketservice.dto.BookingHitoryDTO;
import com.app.footballticketservice.dto.BookingHitoryDTOV2;
import com.app.footballticketservice.dto.CheckTicketSeatDTO;
import com.app.footballticketservice.exception.AppException;
import com.app.footballticketservice.model.PagingContainer;
import com.app.footballticketservice.model.User;
import com.app.footballticketservice.request.payload.BookingPayload;
import com.app.footballticketservice.utils.PagingUtil;
import com.stripe.exception.StripeException;
import jakarta.mail.MessagingException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingService {
    private final NamedParameterJdbcTemplate writeDb;
    private final PaymentService paymentService;
    private final EmailService emailService;

    public BookingService(
            @WriteDB NamedParameterJdbcTemplate writeDb, PaymentService paymentService,
            EmailService emailService
    ) {
        this.writeDb = writeDb;
        this.paymentService = paymentService;
        this.emailService = emailService;
    }


    public PagingContainer<BookingHitoryDTOV2> getBooking(int page, int size, String key) {
        var sql = """
                select b.txn_ref
                     , t.name                                  as ticket_type
                     , concat(home.name, ' vs ', way.name)     as team_match
                     , home.logo                               as home_logo
                     , way.logo                                as away_logo
                     , concat(m.match_date, ' ', m.match_time) as match_time
                     , b.total                                 as total_price
                     , GROUP_CONCAT(s.seat_number)             as seats
                     , b.status                                as booking_status
                     , b.payment_method
                     , u.email
                     , concat(u.first_name, ' ', u.last_name) as user_name
                from bookings b
                         inner join users u on b.user_id = u.user_id
                         inner join type t on b.type_id = t.type_id
                         inner join matches m on b.match_id = m.match_id
                         inner join teams home on m.home_id = home.team_id
                         inner join teams way on m.away_id = way.team_id
                         inner join booking_seat bs on b.booking_id = bs.booking_id
                         inner join seat s on bs.seat_id = s.seat_id
                where true
                    and u.email like :keyword
                   or concat(u.first_name, ' ', u.last_name) like :keyword
                   or b.txn_ref like :keyword
                   or concat(home.name, ' vs ', way.name) like :keyword
                group by b.booking_id, b.created_at
                order by b.created_at desc
                limit :offset, :size;
                """;
        var param = new MapSqlParameterSource()
                .addValue("keyword", "%" + key + "%")
                .addValue("offset", PagingUtil.calculateOffset(page, size))
                .addValue("size", size);
        var result = writeDb.query(sql, param, BeanPropertyRowMapper.newInstance(BookingHitoryDTOV2.class));
        var sqlCount = """
                select count(1)
                from bookings b
                         inner join users u on b.user_id = u.user_id
                         inner join type t on b.type_id = t.type_id
                         inner join matches m on b.match_id = m.match_id
                         inner join teams home on m.home_id = home.team_id
                         inner join teams way on m.away_id = way.team_id
                            inner join booking_seat bs on b.booking_id = bs.booking_id
                            inner join seat s on bs.seat_id = s.seat_id
                where true
                    and u.email like :keyword
                   or concat(u.first_name, ' ', u.last_name) like :keyword
                   or b.txn_ref like :keyword
                   or concat(home.name, ' vs ', way.name) like :keyword;
                """;
        var count = writeDb.queryForObject(sqlCount, param, Long.class);
        return new PagingContainer<>(page, size, count, result);
    }

    public PagingContainer<BookingHitoryDTO> history(User user, int page, int size) {
        var sql = """
                select b.txn_ref
                     , t.name                                  as ticket_type
                     , concat(home.name, ' vs ', way.name)     as team_match
                     , home.logo                               as home_logo
                     , way.logo                                as away_logo
                     , concat(m.match_date, ' ', m.match_time) as match_time
                     , b.total                                 as total_price
                     , GROUP_CONCAT(s.seat_number)             as seats
                     , b.status                                as booking_status
                     , b.payment_method
                from bookings b
                         inner join type t on b.type_id = t.type_id
                         inner join matches m on b.match_id = m.match_id
                         inner join teams home on m.home_id = home.team_id
                         inner join teams way on m.away_id = way.team_id
                         inner join booking_seat bs on b.booking_id = bs.booking_id
                         inner join seat s on bs.seat_id = s.seat_id
                where true
                    and (:userId = -1 OR user_id = :userId)
                group by b.booking_id, b.created_at
                order by b.created_at desc
                limit :offset, :size;
                """;
        var param = new MapSqlParameterSource()
                .addValue("userId", user.getUserId())
                .addValue("offset", PagingUtil.calculateOffset(page, size))
                .addValue("size", size);
        var result = writeDb.query(sql, param, BeanPropertyRowMapper.newInstance(BookingHitoryDTO.class));
        var sqlCount = """
                select count(1)
                from bookings
                where user_id = :userId;
                """;
        var count = writeDb.queryForObject(sqlCount, param, Long.class);
        return new PagingContainer<>(page, size, count, result);
    }

    @Transactional
    public void confirmBooking(String txnRef, boolean success, User user) throws MessagingException {
        var sqlCheck = """
                select status
                from bookings
                WHERE txn_ref = :txnRef;
                """;
        var paramCheck = new MapSqlParameterSource().addValue("txnRef", txnRef);
        var status = writeDb.queryForObject(sqlCheck, paramCheck, (res, i) -> res.getString("status"));
        if (!status.equalsIgnoreCase("pending")) {
            return;
        }

        if (success) {
            var sqlConfirm = """
                    UPDATE bookings
                    set status = 'completed'
                    where txn_ref = :txnRef
                    """;
            writeDb.update(sqlConfirm, paramCheck);

            var sqlSendEmail = """
                    select GROUP_CONCAT(s.seat_number) as seats,
                         home.name                   as home_team,
                         away.name                   as away_team,
                         home.logo                   as home_logo,
                         away.logo                   as away_logo,
                         m.match_date,
                         m.match_time,
                         st.stadium_name             as stadium
                    from bookings b
                             inner join matches m on b.match_id = m.match_id
                             inner join stadium st on m.stadium_id = st.stadium_id
                             inner join teams home on m.home_id = home.team_id
                             inner join teams away on m.away_id = away.team_id
                             inner join booking_seat bs on b.booking_id = bs.booking_id
                             inner join seat s on bs.seat_id = s.seat_id
                    where b.txn_ref = :txnRef
                    group by home.name, away.name, m.match_date, m.match_time, st.stadium_name
                    """;
            writeDb.query(
                           sqlSendEmail,
                           paramCheck,
                           BeanPropertyRowMapper.newInstance(BookSendEmailDTO.class)
                   )
                   .stream()
                   .findFirst().ifPresent(bookSendEmailDTO -> {
                       try {
                           emailService.sendBooking(bookSendEmailDTO, user);
                       } catch (MessagingException | IOException e) {
                           updateFail(paramCheck);
                           throw new AppException("500", "Error sending email");
                       }
                   });
        } else {
            updateFail(paramCheck);
        }
    }

    @Transactional
    protected void updateFail(MapSqlParameterSource param) {
        var sqlCancel = """
                UPDATE bookings
                set status = 'cancelled'
                where txn_ref = :txnRef
                """;
        writeDb.update(sqlCancel, param);

        // update ticket_seat
        var sqlUpdateSeat = """
                update ticket_seat
                set is_booked = 'no'
                where seat_id in (select seat_id
                                  from bookings b
                                           join booking_seat bs on b.booking_id = bs.booking_id
                                  where b.txn_ref = :txnRef);
                """;
        writeDb.update(sqlUpdateSeat, param);
    }

    @Transactional
    public String bookTicket(BookingPayload payload, User user) throws StripeException {
        // Book ticket
        var sqlCheck = """
                  SELECT ts.match_id,
                         ts.seat_id,
                         is_booked,
                         s.seat_number,
                         IFNULL(t.price, 0) as price
                  FROM ticket_seat ts
                           INNER JOIN seat s ON ts.seat_id = s.seat_id
                           INNER JOIN tickets t ON t.type_id = ts.type_id AND t.match_id = ts.match_id
                  WHERE TRUE
                    AND ts.match_id = :matchId
                    AND ts.seat_id IN (:seatIds);
                """;
        var paramCheck = new MapSqlParameterSource()
                .addValue("matchId", payload.getMatchId())
                .addValue(
                        "seatIds",
                        CollectionUtils.isEmpty(payload.getSeatIds()) ? List.of(-1) : payload.getSeatIds()
                );
        var checkResult = writeDb.query(
                sqlCheck,
                paramCheck,
                BeanPropertyRowMapper.newInstance(CheckTicketSeatDTO.class)
        );
        if (CollectionUtils.isNotEmpty(checkResult)) {
            var messageSeatBooked = checkResult.stream()
                                               .filter(CheckTicketSeatDTO::available)
                                               .map(CheckTicketSeatDTO::getSeatNumber)
                                               .reduce((seat1, seat2) -> seat1 + ", " + seat2)
                                               .map(seat -> "Seat(s) " + seat + " already booked")
                                               .orElse(null);
            if (messageSeatBooked != null) {
                throw new AppException("400", messageSeatBooked);
            }
        }
        var totalPrice = checkResult.stream()
                                    .map(CheckTicketSeatDTO::getPrice)
                                    .reduce(BigDecimal::add)
                                    .orElse(BigDecimal.ZERO);
        var keyHolder = new GeneratedKeyHolder();
        var sqlBook = "INSERT INTO bookings(match_id, type_id, user_id, total, txn_ref, payment_method) VALUES (:matchId, :typeId, :userId, :total, :txnRef, :paymentMethod)";
        var txnRef = String.valueOf(System.currentTimeMillis());
        var paramBook = new MapSqlParameterSource()
                .addValue("matchId", payload.getMatchId())
                .addValue("typeId", payload.getTypeId())
                .addValue("userId", user.getUserId())
                .addValue("paymentMethod", payload.getPaymentMethod())
                .addValue("txnRef", txnRef)
                .addValue("total", totalPrice);
        writeDb.update(sqlBook, paramBook, keyHolder);
        var bookingId = keyHolder.getKey().longValue();

        var paramBookingDetail = checkResult.stream()
                                            .map(seat -> new MapSqlParameterSource()
                                                    .addValue("bookingId", bookingId)
                                                    .addValue("seatId", seat.getSeatId())
                                                    .addValue("price", seat.getPrice())
                                            )
                                            .toArray(MapSqlParameterSource[]::new);
        var sqlBookingDetail = "insert into booking_seat(booking_id, seat_id, price) VALUES (:bookingId, :seatId, :price)";
        writeDb.batchUpdate(sqlBookingDetail, paramBookingDetail);

        var sqlUpdateSeat = "UPDATE ticket_seat SET is_booked = 'yes' WHERE match_id = :matchId AND seat_id IN (:seatIds)";
        var paramUpdateSeat = new MapSqlParameterSource()
                .addValue("matchId", payload.getMatchId())
                .addValue(
                        "seatIds",
                        CollectionUtils.isEmpty(payload.getSeatIds()) ? List.of(-1) : payload.getSeatIds()
                );
        writeDb.update(sqlUpdateSeat, paramUpdateSeat);
        var name = checkResult.stream()
                              .map(CheckTicketSeatDTO::getSeatNumber)
                              .reduce((seat1, seat2) -> seat1 + ", " + seat2)
                              .map(seat -> "Payment for seat(s) " + seat)
                              .orElse("");
        if ("vnpay".equals(payload.getPaymentMethod())) {
            return paymentService.vnPay(
                    txnRef,
                    totalPrice.multiply(BigDecimal.valueOf(100)).longValue(),       // remove decimal point in vnPay
                    "Booking ticket"
            );
        }

        return paymentService.stripe(
                user.getEmail(),
                totalPrice.longValue(),
                name,
                txnRef
        );
    }
}
