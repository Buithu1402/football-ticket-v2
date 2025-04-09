export class League {
  constructor(
    public leagueId: number = 0,
    public name: string = '',
    public logo: string = '',
    public matchCount: number = 0,
    public matchToday: number = 0,
    public matchTomorrow: number = 0,
    public matchLive: number = 0,
    public matchPending: number = 0,
    public minPrice: number = 0,
  ) {
  }
}
