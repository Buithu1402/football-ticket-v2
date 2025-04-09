export class MatchDetailDTO {
  constructor(
    public matchId: number = 0,
    public homeTeam: string = '',
    public awayTeam: string = '',
    public homeLogo: string = '',
    public awayLogo: string = '',
    public homeGoal: number = 0,
    public awayGoal: number = 0,
    public matchDate: string = '',
    public matchTime: string = '',
    public leagueName: string = '',
    public stadium: string = '',
    public stadiumImage: string = '',
    public matchStatus: string = '',
    public types: MatchType[] = [],
  ) {
  }
}


export class MatchType {
  constructor(
    public typeId: number = 0,
    public name: string = '',
    public description: string = '',
    public price: number = 0,
    public seats: MatchSeat[] = [],
  ) {
  }
}

export class MatchSeat {
  constructor(
    public seatId: number = 0,
    public seatNumber: string = '',
  ) {
  }
}
