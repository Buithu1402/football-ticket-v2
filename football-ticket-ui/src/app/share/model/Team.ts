export class Team {
  constructor(
    public teamId: number = 0,
    public name: string = '',
    public logo: string = '',
    public stadiumName: string = '',
    public address: string = '',
    public establishedYear: number = 0,
    public matchPlayed: number = 0,
    public win: number = 0,
    public losses: number = 0,
    public draw: number = 0,
    public goals: number = 0,
  ) {
  }
}


export class TeamPayload {
  constructor(
    public teamId: number = 0,
    public name: string = '',
    public logo: string = '',
    public stadiumName: string = '',
    public address: string = '',
    public establishedYear: number = 0,
  ) {
  }
}
