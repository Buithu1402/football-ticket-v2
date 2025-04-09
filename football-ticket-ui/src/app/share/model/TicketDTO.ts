import {SeatDTO} from './SeatDTO';

export class TicketDTO {
  constructor(
    public typeId: number = 0,
    public name: string = '',
    public matchId: number = 0,
    public price: number = 0,
    public description: string = '',
    public seats: SeatDTO[] = [],
  ) {
  }
}
