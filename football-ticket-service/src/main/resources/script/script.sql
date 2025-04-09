drop database if exists `ticket_db`;
create database if not exists `ticket_db`;
use `ticket_db`;

drop table if exists `system_settings`;
create table if not exists `system_settings`
(
    `setting_id` int(11)      not null auto_increment,
    `name`       varchar(255) not null,
    `value`      text,
    `created_at` timestamp default current_timestamp,
    `updated_at` timestamp default current_timestamp on update current_timestamp,
    primary key (`setting_id`),
    unique key `name` (`name`),
    index (`name`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `users`;
create table if not exists `users`
(
    `user_id`    int(11)                          not null auto_increment,
    `email`      varchar(255)                     not null,
    `password`   varchar(500)                     not null,
    `first_name` varchar(50)                      not null,
    `last_name`  varchar(50)                      not null,
    `gender`     enum ('male', 'female', 'other') default 'other',
    `avatar`     LONGTEXT,
    `role`       ENUM ('admin', 'user')                 default 'user',
    `status`     ENUM ('active', 'inactive', 'blocked') default 'active',
    `created_at` timestamp                              default current_timestamp,
    `updated_at` timestamp                              default current_timestamp on update current_timestamp,
    primary key (`user_id`),
    unique key `email` (`email`),
    index (`email`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `otp`;
create table if not exists `otp`
(
    `otp_id`     int(11)      not null auto_increment,
    `email`      varchar(255) not null,
    `otp`        varchar(6)   not null,
    `expired_at` bigint,
    `created_at` timestamp default current_timestamp,
    primary key (`otp_id`),
    unique key (`email`),
    foreign key (`email`) references `users` (`email`),
    index (`email`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `forget_password_token`;
create table if not exists `forget_password_token`
(
    `token_id`   int(11) primary key auto_increment,
    `email`      varchar(255) not null,
    `token`      varchar(500) not null,
    `expired_at` bigint unsigned,
    foreign key (`email`) references `users` (`email`),
    index (`email`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `teams`;
create table if not exists `teams`
(
    `team_id`          int(11)      not null auto_increment,
    `name`             varchar(255) not null,
    `description`      text,
    `address`          text,
    `logo`             longtext,
    `established_year` int,
    `stadium_map`      longtext,
    `stadium_name`     varchar(255),
    `match_played`     int                         default 0,
    `win`              int                         default 0,
    `losses`           int                         default 0,
    `draw`             int                         default 0,
    `goals`            int                         default 0,
    `status`           enum ('active', 'inactive') default 'active',
    `created_at`       timestamp                   default current_timestamp,
    `updated_at`       timestamp                   default current_timestamp on update current_timestamp,
    primary key (`team_id`),
    unique key `name` (`name`),
    index (`name`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `players`;
create table if not exists `players`
(
    `player_id` int(11)      not null auto_increment,
    `team_id`   int(11),
    `name`      varchar(255) not null,
    `position`  varchar(255),
    `no`        int,
    `avatar`    longtext,
    `nation`    text,
    `match`     int                         default 0,
    `goal`      int                         default 0,
    `status`    enum ('active', 'inactive') default 'active',
    foreign key (`team_id`) references `teams` (`team_id`),
    primary key (`player_id`),
    index (`team_id`)
);

drop table if exists `leagues`;
create table if not exists `leagues`
(
    `league_id`  int(11)      not null auto_increment,
    `name`       varchar(255) not null,
    `logo`       longtext,
    `status`     enum ('active', 'inactive') default 'active',
    `created_at` timestamp                   default current_timestamp,
    `updated_at` timestamp                   default current_timestamp on update current_timestamp,
    primary key (`league_id`),
    unique key `name` (`name`),
    index (`name`)
) engine = InnoDB
  default charset = utf8;

drop table if exists ticket_seat;
drop table if exists `bookings`;
drop table if exists `tickets`;
drop table if exists `matches`;
drop table if exists `seat`;
drop table if exists `stadium`;

create table if not exists stadium
(
    stadium_id   int auto_increment
        primary key,
    stadium_name varchar(255),
    image        longtext,
    address      text,
    status       enum ('active', 'inactive') default 'active',
    created_at   timestamp                   default CURRENT_TIMESTAMP,
    updated_at   timestamp                   default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP
);

create table if not exists `seat`
(
    seat_id     int auto_increment
        primary key,
    stadium_id  int,
    seat_number VARCHAR(255),
    status      enum ('active', 'inactive') default 'active',
    created_at  timestamp                   default CURRENT_TIMESTAMP,
    updated_at  timestamp                   default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
    constraint seat_stadium_id_fk
        foreign key (stadium_id) references stadium (stadium_id)
);

create table if not exists `matches`
(
    `match_id`   int(11) not null auto_increment,
    `league_id`  int(11),
    `stadium_id` int(11),
    `home_id`    int(11),
    `away_id`    int(11),
    `home_goal`  int,
    `away_goal`  int,
    `match_date` date,
    `match_time` time,
    `widget`     text,
    `status`     enum ('pending', 'live', 'completed') default 'pending',
    `created_at` timestamp                             default current_timestamp,
    `updated_at` timestamp                             default current_timestamp on update current_timestamp,
    foreign key (`home_id`) references `teams` (`team_id`),
    foreign key (`away_id`) references `teams` (`team_id`),
    foreign key (`league_id`) references `leagues` (`league_id`),
    foreign key (`stadium_id`) references `stadium` (`stadium_id`),
    primary key (`match_id`),
    index (`home_id`),
    index (`away_id`),
    index (`league_id`),
    index (`stadium_id`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `type`;
create table if not exists `type`
(
    `type_id`    int(11)      not null auto_increment,
    `name`       varchar(255) not null,
    `created_at` timestamp default current_timestamp,
    `updated_at` timestamp default current_timestamp on update current_timestamp,
    primary key (`type_id`),
    unique key `name` (`name`),
    index (`name`)
) engine = InnoDB
  default charset = utf8;

drop table if exists `tickets`;
create table if not exists `tickets`
(
    `match_id`    int(11),
    `type_id`     int(11),
    `status`      enum ('active', 'inactive') default 'active',
    `price`       decimal(10, 2),
    `description` text,
    `created_at`  timestamp                   default current_timestamp,
    `updated_at`  timestamp                   default current_timestamp on update current_timestamp,
    foreign key (`match_id`) references `matches` (`match_id`),
    foreign key (`type_id`) references `type` (`type_id`),
    primary key (`match_id`, `type_id`),
    index (`match_id`)
) engine = InnoDB
  default charset = utf8;

create table if not exists ticket_seat
(
    `match_id` int(11),
    `type_id`  int(11),
    seat_id    int,
    status     enum ('active', 'inactive') default 'active',
    is_booked  enum ('yes', 'no')          default 'no',
    created_at timestamp                   default current_timestamp,
    updated_at timestamp                   default current_timestamp on update current_timestamp,
    foreign key (`match_id`) references `matches` (`match_id`),
    foreign key (`type_id`) references `type` (`type_id`),
    foreign key (`seat_id`) references `seat` (`seat_id`),
    primary key (`match_id`, `seat_id`),
    index (`match_id`, `seat_id`)
);

create table if not exists `bookings`
(
    `booking_id`     int(11) not null auto_increment,
    `match_id`       int(11),
    `type_id`        int(11),
    `user_id`        int(11),
    `total`          decimal(10, 2),
    `txn_ref`        varchar(255),
    `payment_method` enum ('vnpay', 'visa'),
    `status`         enum ('pending', 'completed', 'cancelled') default 'pending',
    `created_at`     timestamp                                  default current_timestamp,
    `updated_at`     timestamp                                  default current_timestamp on update current_timestamp,
    foreign key (`user_id`) references `users` (`user_id`),
    foreign key (`match_id`) references `matches` (`match_id`),
    foreign key (`type_id`) references `type` (`type_id`),
    primary key (`booking_id`),
    index (`match_id`, `type_id`, `user_id`),
    index (`user_id`)
) engine = InnoDB
  default charset = utf8;

create table if not exists booking_seat
(
    booking_id int,
    seat_id    int,
    price      decimal(10, 2),
    status     enum ('active', 'inactive') default 'active',
    created_at timestamp                   default current_timestamp,
    updated_at timestamp                   default current_timestamp on update current_timestamp,
    foreign key (booking_id) references bookings (booking_id),
    foreign key (seat_id) references seat (seat_id),
    primary key (booking_id, seat_id)
);
