export class TeamDetailDTO {
  constructor(
    public lastResult: LastResultDTO = new LastResultDTO(),
    public teamDetail: TeamDetail = new TeamDetail(),
    public last5Match: LastResultDTO[] = [],
  ) {
  }
}

export class LastResultDTO {
  constructor(
    public homeTeam: string = '',
    public awayTeam: string = '',
    public homeScore: string = '',
    public awayScore: string = '',
    public matchDatetime: string = '',
    public leagueName: string = '',
    public homeLogo: string = '',
    public awayLogo: string = '',
    public status: string = '',
  ) {
  }
}

export class TeamDetail {
  constructor(
    public name: string = '',
    public logo: string = '',
    public address: string = '',
    public goals: string = '',
    public win: string = '',
    public draw: string = '',
    public losses: string = '',
    public matchPlayed: string = '',
    public establishedYear: string = '',
  ) {
  }
}
