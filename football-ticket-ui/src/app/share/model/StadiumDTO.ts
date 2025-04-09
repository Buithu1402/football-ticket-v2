import {SeatDTO} from './SeatDTO';

export class StadiumDTO {
  constructor(
    public stadiumId: number = -1,
    public stadiumName: string = '',
    public image: string = '',
    public address: string = '',
  ) {
  }
}

export class StadiumParam {
  constructor(
    public stadiumId: number = -1,
    public stadiumName: string = '',
    public address: string = '',
    public image: string = '',
    public seats2: SeatDTO[] = [],
    public seats: SeatParam[] = [new SeatParam()],
  ) {
  }
}


export class SeatParam {
  constructor(
    public seatId: number = -1,
    public prefix: string = '',
    public start: number = 1,
    public end: number = 1,
    public typeId: number = 1,
  ) {
  }
}
