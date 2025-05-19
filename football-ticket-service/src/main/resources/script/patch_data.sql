insert into system_settings(name, value)
VALUES ('VN_PAY_RETURN_URL', 'http://localhost:4200/#/vn-pay'),
('STRIPE_RETURN_URL', 'http://localhost:4200/#/thank-you?q=stripe'),
('STRIPE_API_KEY','123'),
('CLOUD_NAME','123'),
('API_KEY','123'),
('API_SECRET','123');


INSERT INTO system_settings(name, value) VALUES ('BILL_EMAIL', '<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Football Ticket</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            background-color: #f4f4f4;
            font-family: Arial, sans-serif;
        }

        .container {
            width: 100%;
            padding: 20px 0;
            text-align: center;
        }

        .ticket {
            width: 380px;
            background: #fff;
            border-radius: 10px;
            overflow: hidden;
            border: 3px solid #333;
            box-shadow: 2px 4px 10px rgba(0, 0, 0, 0.1);
            margin: 0 auto;
        }

        .ticket-header {
            background: #1e3a8a;
            color: #fff;
            padding: 15px;
            text-align: center;
            font-size: 18px;
            font-weight: bold;
        }

        .teams {
            width: 100%;
            text-align: center;
            padding: 15px;
            background: #e5e7eb;
        }

        .team-logo {
            width: 60px;
            height: 60px;
            vertical-align: middle;
        }

        .vs {
            font-size: 20px;
            font-weight: bold;
            padding: 0 10px;
        }

        .match-info {
            padding: 15px;
            text-align: center;
            font-size: 14px;
            background: #fff;
        }

        .ticket-details {
            background: #f3f4f6;
            padding: 15px;
            font-size: 14px;
            text-align: center;
        }

        .ticket-details p {
            margin: 5px 0;
        }

        .qr-code img {
            width: 100%;
            height: 100%;
            display: block;
            border-radius: 5px;
        }
    </style>
</head>

<body>
<table role="presentation" class="container" cellspacing="0" cellpadding="0" border="0">
    <tr>
        <td align="center">
            <table role="presentation" class="ticket" cellspacing="0" cellpadding="0" border="0">
                <tr>
                    <td class="ticket-header">FOOTBALL MATCH TICKET</td>
                </tr>
                <tr>
                    <td class="teams">
                        <img src="$model.homeLogo$" alt="Home Team" class="team-logo">
                        <span class="vs">VS</span>
                        <img src="$model.awayLogo$" alt="Away Team" class="team-logo">
                    </td>
                </tr>
                <tr>
                    <td class="match-info">
                        <p><strong>$model.homeTeam$ vs $model.awayTeam$</strong></p>
                        <p><strong>Ngày:</strong> $model.matchDate$</p>
                        <p><strong>Giờ:</strong> $model.matchTime$</p>
                        <p><strong>Địa điểm:</strong> $model.stadium$</p>
                    </td>
                </tr>
                <tr>
                    <td class="ticket-details">
                        <p><strong>Seat:</strong> $model.seats$</p>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>

</html>
');
