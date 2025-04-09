package com.app.footballticketservice.request.response;

import com.app.footballticketservice.dto.LastResultDTO;
import com.app.footballticketservice.dto.TeamDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDetailResponse {
    LastResultDTO lastResult;
    TeamDetailDTO teamDetail;
    List<LastResultDTO> last5Match;

    public TeamDetailResponse withLastResult(LastResultDTO lastResult) {
        this.lastResult = lastResult;
        return this;
    }

    public TeamDetailResponse withTeamDetail(TeamDetailDTO teamDetail) {
        this.teamDetail = teamDetail;
        return this;
    }

    public TeamDetailResponse withLast5Match(List<LastResultDTO> last5Match) {
        this.last5Match = last5Match;
        return this;
    }
}
