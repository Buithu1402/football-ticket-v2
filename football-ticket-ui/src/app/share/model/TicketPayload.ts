import {SeatDTO} from './SeatDTO';

export class TicketPayload {
  constructor(
    public matchId: number = 0,
    public typeId: number = 0,
    public typeName: string = '',
    public price: number | null = null,
    public priceMask: number | null = null,
    public description: string = '',
    public validPrice: boolean = true,
    public currentSeats: string = '',
    public seatIds: number[] = [],
    public seats: SeatDTO[] = [],
    public availableSeats: SeatDTO[] = []
  ) {
  }
}
