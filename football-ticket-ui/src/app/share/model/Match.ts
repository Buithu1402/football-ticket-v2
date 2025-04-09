export class Match {
  constructor(
    public matchId: number = 0,
    public homeTeamId: string = '',
    public awayTeamId: string = '',
    public stadiumId: number = 0,
    public homeTeamName: string = '',
    public awayTeamName: string = '',
    public stadiumName: string = '',
    public homeGoal: number = 0,
    public awayGoal: number = 0,
    public status: string = '',
    public homeLogo: string = '',
    public awayLogo: string = '',
    public matchDate: string = '',
    public matchTime: string = '',
    public leagueId: number = 0,
  ) {
  }
}
