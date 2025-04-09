package com.app.footballticketservice.request.response;

import com.app.footballticketservice.dto.MatchDetailDTO;
import com.app.footballticketservice.dto.TypeDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MatchDetailResponse extends MatchDetailDTO {
    List<TicketSeatResponse> types;

    public MatchDetailResponse(MatchDetailDTO matchDetailDTO, List<TicketSeatResponse> types) {
        super(
                matchDetailDTO.getMatchId(),
                matchDetailDTO.getHomeTeam(),
                matchDetailDTO.getAwayTeam(),
                matchDetailDTO.getHomeLogo(),
                matchDetailDTO.getAwayLogo(),
                matchDetailDTO.getHomeGoal(),
                matchDetailDTO.getAwayGoal(),
                matchDetailDTO.getMatchDate(),
                matchDetailDTO.getMatchTime(),
                matchDetailDTO.getLeagueName(),
                matchDetailDTO.getStadium(),
                matchDetailDTO.getStadiumImage(),
                matchDetailDTO.getMatchStatus()
        );
        this.types = types;
    }
}
