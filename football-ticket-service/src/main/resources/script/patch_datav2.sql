alter table seat
    add column type_id int,
    add constraint fk_type_id foreign key (type_id) references type (type_id),
    add index idx_type_id (type_id);


with seat_book as (select ts.seat_id, max(ts.match_id) as mid
                   from ticket_seat ts
                   group by ts.seat_id
                   order by ts.seat_id),
     seat_type as (select s.seat_id, MAX(IF(ts.type_id IS NULL, 1, ts.type_id)) as type_id
                   from seat s
                            left join ticket_seat ts on s.seat_id = ts.seat_id
                            left join seat_book sb on sb.seat_id = s.seat_id
                   where s.type_id IS NULL
                   GROUP BY s.seat_id)
UPDATE seat se
    INNER JOIN seat_type st ON st.seat_id = se.seat_id
SET se.type_id = st.type_id
WHERE se.type_id IS NULL
  AND st.type_id IS NOT NULL;
